package com.ilp.restapi.service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ilp.restapi.data.AvailabilitySlotDTO;
import com.ilp.restapi.data.CalcDeliveryPathResponseDTO;
import com.ilp.restapi.data.CapabilityDTO;
import com.ilp.restapi.data.PathResult;
import com.ilp.restapi.data.DeliveryFlightPathDTO;
import com.ilp.restapi.data.DroneAvailabilityDTO;
import com.ilp.restapi.data.DroneDTO;
import com.ilp.restapi.data.DronePathDTO;
import com.ilp.restapi.data.LngLatDTO;
import com.ilp.restapi.data.MedDispatchRecDTO;
import com.ilp.restapi.data.QueryConditionDTO;
import com.ilp.restapi.data.RequirementsDTO;
import com.ilp.restapi.data.RestrictedAreaDTO;
import com.ilp.restapi.data.ServicePointAvailabilityDTO;
import com.ilp.restapi.data.ServicePointDTO;
import com.ilp.restapi.client.IlpClient;

@Service
public class DroneService {
  private final IlpClient ilpClient;

  public DroneService(IlpClient ilpClient) {
      this.ilpClient = ilpClient;
  }

  private static final double STEP_SIZE = 0.00015;
  private static final double CLOSE_EPSILON = STEP_SIZE; // “close enough” to target
  private static final int MAX_MOVE_CAP = 20000; // safety guard against infinte looping

  /**
   * Returns IDs of drones whose capability.cooling matches the given state param.
   * - Accepts only "true" or "false" (case-insensitive). Otherwise returns [].
   * - Always returns a list (possibly empty). The controller will always reply 200.
   */
  public List<Integer> findIdsByCooling(String stateParam) {
      if (!isBooleanLike(stateParam)) {
          return List.of();
      }
      boolean desired = Boolean.parseBoolean(stateParam.toLowerCase(Locale.ROOT));

      List<DroneDTO> drones = ilpClient.fetchDrones();

      return drones.stream()
              .filter(d -> {
                  CapabilityDTO cap = d.getCapability();
                  return cap != null && cap.isCooling() == desired;
              })
              .map(DroneDTO::getId)
              .collect(Collectors.toList());
  }

  public DroneDTO findDroneById(int id) {
      List<DroneDTO> drones = ilpClient.fetchDrones();

      return drones.stream()
              .filter(d -> d.getId() == id)
              .findFirst()
              .orElse(null);   // controller decides whether to return 404
  }

  private boolean isBooleanLike(String s) {
      if (s == null) return false;
      String v = s.trim().toLowerCase(Locale.ROOT);
      return "true".equals(v) || "false".equals(v);
  }

  public List<Integer> queryAsPath(String attribute, String value) {
      List<DroneDTO> drones = ilpClient.fetchDrones();

      return drones.stream()
              .filter(d -> matchAttribute(d, attribute, "=", value))
              .map(DroneDTO::getId)
              .toList();
  }

  public List<Integer> query(List<QueryConditionDTO> conditions) {
      List<DroneDTO> drones = ilpClient.fetchDrones();

      // If no conditions, return all drones
      if (conditions == null || conditions.isEmpty()) {
          return drones.stream()
                  .map(DroneDTO::getId)
                  .toList();
      }

      return drones.stream()
              .filter(d -> matchesAllConditions(d, conditions))
              .map(DroneDTO::getId)
              .toList();
  }

  private boolean matchesAllConditions(DroneDTO drone, List<QueryConditionDTO> conditions) {
      return conditions.stream().allMatch(cond ->
              matchAttribute(
                      drone,
                      cond.getAttribute(),
                      cond.getOperator(),
                      cond.getValue()
              )
      );
  }

  private boolean matchAttribute(DroneDTO drone, String attribute, String operator, String value) {
      if (drone == null || attribute == null || operator == null || value == null) {
          return false;
      }

      String attr = attribute.trim().toLowerCase(Locale.ROOT);
      String op = operator.trim();
      CapabilityDTO cap = drone.getCapability();

      switch (attr) {
          // ----------------- TOP LEVEL FIELDS -----------------
          case "id": {
              try {
                  int v = Integer.parseInt(value);
                  return compareInts(drone.getId(), v, op);
              } catch (NumberFormatException e) {
                  return false;
              }
          }

          case "name":
              return compareStrings(drone.getName(), value, op);

          // ----------------- CAPABILITY FIELDS -----------------
          case "cooling":
              return cap != null && compareBooleans(cap.isCooling(), value, op);

          case "heating":
              return cap != null && compareBooleans(cap.isHeating(), value, op);

          case "capacity":
              if (cap == null) return false;
              try {
                  double v = Double.parseDouble(value);
                  return compareDoubles(cap.getCapacity(), v, op);
              } catch (NumberFormatException e) {
                  return false;
              }

          case "maxmoves":
              if (cap == null) return false;
              try {
                  int v = Integer.parseInt(value);
                  return compareInts(cap.getMaxMoves(), v, op);
              } catch (NumberFormatException e) {
                  return false;
              }

          case "costpermove":
              if (cap == null) return false;
              try {
                  double v = Double.parseDouble(value);
                  return compareDoubles(cap.getCostPerMove(), v, op);
              } catch (NumberFormatException e) {
                  return false;
              }

          case "costinitial":
              if (cap == null) return false;
              try {
                  double v = Double.parseDouble(value);
                  return compareDoubles(cap.getCostInitial(), v, op);
              } catch (NumberFormatException e) {
                  return false;
              }

          case "costfinal":
              if (cap == null) return false;
              try {
                  double v = Double.parseDouble(value);
                  return compareDoubles(cap.getCostFinal(), v, op);
              } catch (NumberFormatException e) {
                  return false;
              }

          default:
              // Unknown attribute -> no match
              return false;
      }
  }

  private boolean compareInts(int field, int value, String op) {
      return switch (op) {
          case "=" -> field == value;
          case "!=" -> field != value;
          case "<" -> field < value;
          case ">" -> field > value;
          default -> false;
      };
  }

  private boolean compareDoubles(double field, double value, String op) {
      return switch (op) {
          case "=" -> Double.compare(field, value) == 0;
          case "!=" -> Double.compare(field, value) != 0;
          case "<" -> field < value;
          case ">" -> field > value;
          default -> false;
      };
  }

  private boolean compareBooleans(boolean field, String value, String op) {
      boolean v;
      if ("true".equalsIgnoreCase(value)) {
          v = true;
      } else if ("false".equalsIgnoreCase(value)) {
          v = false;
      } else {
          return false; // not a valid boolean
      }

      return switch (op) {
          case "=" -> field == v;
          case "!=" -> field != v;
          default -> false; // < and > don't make sense for booleans
      };
  }

  private boolean compareStrings(String field, String value, String op) {
      // Safely handle nulls
      String f = field == null ? null : field.trim();
      String v = value == null ? null : value.trim();

      return switch (op) {
          case "=" -> f != null && v != null && f.equalsIgnoreCase(v);
          case "!=" -> {
              if (f == null && v == null) yield false;
              if (f == null || v == null) yield true;
              yield !f.equalsIgnoreCase(v);
          }
          default -> false; // No < or > for strings in this spec
      };
  }

  public List<Integer> queryAvailableDrones(List<MedDispatchRecDTO> dispatches) {
      List<DroneDTO> drones = ilpClient.fetchDrones();
      List<ServicePointAvailabilityDTO> availabilityData = ilpClient.fetchAvailability();
      List<ServicePointDTO> servicePoints = ilpClient.fetchServicePoints();

      // droneId -> availability slots
      Map<Integer, List<AvailabilitySlotDTO>> availabilityByDroneId =
              buildAvailabilityMap(availabilityData);

      // servicePointId -> location
      Map<Integer, LngLatDTO> servicePointLocationById =
              servicePoints.stream()
                      .filter(sp -> sp.getId() != null && sp.getLocation() != null)
                      .collect(Collectors.toMap(
                              ServicePointDTO::getId,
                              ServicePointDTO::getLocation
                      ));

      // droneId -> dispatch/service point location
      Map<Integer, LngLatDTO> dispatchPointByDroneId =
              buildDispatchPointMap(availabilityData, servicePointLocationById);

      if (dispatches == null || dispatches.isEmpty()) {
          // up to you: return all drones or []
          return drones.stream()
                  .map(DroneDTO::getId)
                  .toList();
      }

      return drones.stream()
              .filter(drone -> canHandleAllDispatches(
                      drone,
                      dispatches,
                      availabilityByDroneId.get(drone.getId()),
                      dispatchPointByDroneId.get(drone.getId())
              ))
              .map(DroneDTO::getId)
              .toList();
  }
  
  private boolean canHandleAllDispatches(DroneDTO drone,
                                         List<MedDispatchRecDTO> dispatches,
                                         List<AvailabilitySlotDTO> slots,
                                         LngLatDTO dispatchPoint) {
      return dispatches.stream().allMatch(rec ->
            matchesDispatch(drone, rec, slots, dispatchPoint, dispatchPoint)
            && canAffordDispatch(drone, rec, dispatchPoint));
  }

  /**
 * Estimate dispatch cost using straight-line distance and filter out drones
 * whose estimated cost exceeds requirements.maxCost.
 *
 * Cost model (round trip):
 *   moves ≈ (2 * distance(base, delivery)) / STEP_SIZE
 *   cost  = moves * costPerMove + costInitial + costFinal
 *
 * If maxCost is null -> no cost constraint.
 */
private boolean canAffordDispatch(DroneDTO drone,
                                  MedDispatchRecDTO rec,
                                  LngLatDTO base) {

    if (drone == null || rec == null || base == null || rec.getDelivery() == null) {
        return false;
    }

    RequirementsDTO req = rec.getRequirements();
    if (req == null || req.getMaxCost() == null) {
        return true; // no maxCost constraint
    }

    CapabilityDTO cap = drone.getCapability();
    if (cap == null) return false;

    double oneWayDist = euclideanDistance(base, rec.getDelivery());
    double roundTripDist = 2 * oneWayDist;

    double estMoves = roundTripDist / STEP_SIZE;

    double estCost =
            estMoves * cap.getCostPerMove()
            + cap.getCostInitial()
            + cap.getCostFinal();

    return estCost <= req.getMaxCost();
}

public CalcDeliveryPathResponseDTO calcDeliveryPath(List<MedDispatchRecDTO> dispatches) {

    CalcDeliveryPathResponseDTO resp = new CalcDeliveryPathResponseDTO();
    resp.setDronePaths(new ArrayList<>());
    resp.setTotalCost(0.0);
    resp.setTotalMoves(0);

    if (dispatches == null || dispatches.isEmpty()) {
        return resp;
    }

    // Fetch environment data once
    List<DroneDTO> drones = ilpClient.fetchDrones();
    Map<Integer, DroneDTO> droneById = drones.stream()
            .collect(Collectors.toMap(DroneDTO::getId, d -> d));

    List<ServicePointAvailabilityDTO> availabilityData = ilpClient.fetchAvailability();
    List<ServicePointDTO> servicePoints = ilpClient.fetchServicePoints();
    List<RestrictedAreaDTO> restrictedAreas = ilpClient.fetchRestrictedAreas();

    // droneId -> availability slots
    Map<Integer, List<AvailabilitySlotDTO>> availabilityByDroneId =
            buildAvailabilityMap(availabilityData);

    // servicePointId -> location
    Map<Integer, LngLatDTO> servicePointLocationById =
            servicePoints.stream()
                    .filter(sp -> sp.getId() != null && sp.getLocation() != null)
                    .collect(Collectors.toMap(
                            ServicePointDTO::getId,
                            ServicePointDTO::getLocation
                    ));

      // Group dispatches by nearest feasible service point
      Map<Integer, List<MedDispatchRecDTO>> byServicePoint = new HashMap<>();
      for (MedDispatchRecDTO rec : dispatches) {
          Integer spId = findBestServicePoint(rec, availabilityData, availabilityByDroneId, droneById, servicePointLocationById);
          if (spId == null) continue; // skip if no feasible service point
          byServicePoint.computeIfAbsent(spId, k -> new ArrayList<>()).add(rec);
      }

      int globalMoves = 0;
      double globalCost = 0.0;

      for (Map.Entry<Integer, List<MedDispatchRecDTO>> entry : byServicePoint.entrySet()) {
          Integer spId = entry.getKey();
          List<MedDispatchRecDTO> group = entry.getValue();
          if (group.isEmpty()) continue;

          LngLatDTO base = servicePointLocationById.get(spId);
          if (base == null) continue;

          // Drones based at this service point
          List<Integer> droneIdsAtSp = dronesAtServicePoint(spId, availabilityData);
          List<Integer> unassigned = new ArrayList<>(group.stream().map(MedDispatchRecDTO::getId).toList());

          while (!unassigned.isEmpty()) {
              RouteResult bestRoute = null;
              int chosenDroneId = -1;

              for (Integer droneId : droneIdsAtSp) {
                  DroneDTO drone = droneById.get(droneId);
                  if (drone == null) continue;

                  List<AvailabilitySlotDTO> slots = availabilityByDroneId.get(droneId);
                  RouteResult route = buildRouteForDrone(drone, base, slots, group, unassigned, restrictedAreas);

                  if (route != null && !route.deliveryPaths.isEmpty()) {
                      if (bestRoute == null || route.totalMoves < bestRoute.totalMoves) {
                          bestRoute = route;
                          chosenDroneId = droneId;
                      }
                  }
              }

              if (bestRoute == null || chosenDroneId == -1) {
                  break; // no further assignment possible at this service point
              }

              // Remove assigned ids from this group's unassigned list
              unassigned.removeAll(bestRoute.assignedRecIds);

              // Totals
              globalMoves += bestRoute.totalMoves;
              globalCost += bestRoute.totalCost;

              // Add to response
              DronePathDTO dp = new DronePathDTO();
              dp.setDroneId(chosenDroneId == -1 ? null : Integer.toString(chosenDroneId));
              dp.setDeliveries(bestRoute.deliveryPaths);
              resp.getDronePaths().add(dp);
          }
      }

      resp.setTotalMoves(globalMoves);
      resp.setTotalCost(globalCost);

      return resp;
  }

  public String calcDeliveryPathAsGeoJson(List<MedDispatchRecDTO> dispatches) {
        CalcDeliveryPathResponseDTO full = calcDeliveryPath(dispatches);

        List<double[]> coords = new ArrayList<>();

        if (full.getDronePaths() != null) {
            for (DronePathDTO dp : full.getDronePaths()) {
                if (dp == null || dp.getDeliveries() == null || dp.getDeliveries().isEmpty()) continue;

                for (DeliveryFlightPathDTO del : dp.getDeliveries()) {
                    if (del == null || del.getFlightPath() == null) continue;
                    for (LngLatDTO pt : del.getFlightPath()) {
                        if (pt == null) continue;
                        coords.add(new double[]{pt.getLng(), pt.getLat()});
                    }
                }
                break; // only need one route per spec
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{\"type\":\"LineString\",\"coordinates\":[");
        for (int i = 0; i < coords.size(); i++) {
            double[] c = coords.get(i);
            sb.append("[").append(c[0]).append(",").append(c[1]).append("]");
            if (i < coords.size() - 1) sb.append(",");
        }
        sb.append("]}");
        return sb.toString();
    }

  // -------------------- ROUTE BUILDING (CHAINING) --------------------

  private Integer findBestServicePoint(MedDispatchRecDTO rec,
                                       List<ServicePointAvailabilityDTO> availabilityData,
                                       Map<Integer, List<AvailabilitySlotDTO>> availabilityByDroneId,
                                       Map<Integer, DroneDTO> droneById,
                                       Map<Integer, LngLatDTO> servicePointLocationById) {

      if (rec == null || rec.getDelivery() == null) return null;

      double bestDist = Double.MAX_VALUE;
      Integer bestSp = null;

      for (ServicePointAvailabilityDTO sp : availabilityData) {
          LngLatDTO spLoc = servicePointLocationById.get(sp.getServicePointId());
          if (spLoc == null || sp.getDrones() == null) continue;

          boolean anyFeasible = false;
          for (DroneAvailabilityDTO da : sp.getDrones()) {
              Integer droneId = parseDroneId(da.getId());
              if (droneId == null) continue;
              DroneDTO drone = droneById.get(droneId);
              if (drone == null) continue;

              List<AvailabilitySlotDTO> slots = availabilityByDroneId.get(droneId);
              if (matchesDispatch(drone, rec, slots, spLoc, spLoc)) {
                  anyFeasible = true;
                  break;
              }
          }

          if (!anyFeasible) continue;

          double d = euclideanDistance(spLoc, rec.getDelivery());
          if (d < bestDist) {
              bestDist = d;
              bestSp = sp.getServicePointId();
          }
      }
      return bestSp;
  }

  private List<Integer> dronesAtServicePoint(int servicePointId, List<ServicePointAvailabilityDTO> availabilityData) {
      List<Integer> ids = new ArrayList<>();
      for (ServicePointAvailabilityDTO sp : availabilityData) {
          if (sp.getServicePointId() != servicePointId || sp.getDrones() == null) continue;
          for (DroneAvailabilityDTO da : sp.getDrones()) {
              Integer id = parseDroneId(da.getId());
              if (id != null) {
                  ids.add(id);
              }
          }
      }
      return ids;
  }

  private Integer parseDroneId(String raw) {
      if (raw == null) return null;
      try {
          return Integer.parseInt(raw);
      } catch (NumberFormatException ex) {
          return null;
      }
  }

  private RouteResult buildRouteForDrone(DroneDTO drone,
                                        LngLatDTO base,
                                        List<AvailabilitySlotDTO> slots,
                                        List<MedDispatchRecDTO> allRecsForServicePoint,
                                        List<Integer> unassignedIds,
                                        List<RestrictedAreaDTO> restrictedAreas) {

      CapabilityDTO cap = drone.getCapability();
      if (cap == null) return null;

      int maxMoves = resolveMaxMoves(cap);
      LngLatDTO current = copyPoint(base);

      int movesUsed = 0;
      double variableCost = 0.0;

      List<DeliveryFlightPathDTO> deliveryPaths = new ArrayList<>();
      List<Integer> assignedRecIds = new ArrayList<>();

      while (true) {
          MedDispatchRecDTO best = null;
          PathResult bestPath = null;

          for (MedDispatchRecDTO rec : allRecsForServicePoint) {
              if (rec == null || rec.getDelivery() == null) continue;
              if (!unassignedIds.contains(rec.getId())) continue;

              if (!matchesDispatch(drone, rec, slots, base, current)) continue;

              PathResult toDelivery = computePath(current, rec.getDelivery(), cap, restrictedAreas);
              if (toDelivery.getMoves() > maxMoves) continue;

            PathResult backHome = computePath(rec.getDelivery(), base, cap, restrictedAreas);

            RequirementsDTO req = rec.getRequirements();
            if (req != null && req.getMaxCost() != null) {
                double legCost = cap.getCostInitial() + cap.getCostFinal()
                        + (toDelivery.getMoves() + backHome.getMoves()) * cap.getCostPerMove();
                if (legCost > req.getMaxCost()) continue;
            }

            int tentativeMoves = movesUsed + toDelivery.getMoves() + backHome.getMoves();
            if (tentativeMoves > maxMoves) continue;

              if (best == null || toDelivery.getMoves() < bestPath.getMoves()) {
                  best = rec;
                  bestPath = toDelivery;
              }
          }

          if (best == null || bestPath == null) {
              break;
          }

          List<LngLatDTO> segPath = new ArrayList<>(bestPath.getPath());
          if (!segPath.isEmpty()) {
              segPath.add(copyPoint(segPath.get(segPath.size() - 1))); // hover
          }

          DeliveryFlightPathDTO dpath = new DeliveryFlightPathDTO();
          dpath.setDeliveryId(best.getId());
          dpath.setFlightPath(segPath);
          deliveryPaths.add(dpath);
          assignedRecIds.add(best.getId());
          unassignedIds.remove(best.getId());

          movesUsed += bestPath.getMoves();
          variableCost += bestPath.getMoves() * cap.getCostPerMove();

          current = segPath.isEmpty()
                  ? best.getDelivery()
                  : segPath.get(segPath.size() - 1);
      }

      if (deliveryPaths.isEmpty()) return null;

      // Return leg
      PathResult backPath = computePath(current, base, cap, restrictedAreas);
      if (backPath.getMoves() > maxMoves || movesUsed + backPath.getMoves() > maxMoves) {
          return null; // infeasible to return
      }
      movesUsed += backPath.getMoves();
      variableCost += backPath.getMoves() * cap.getCostPerMove();

      DeliveryFlightPathDTO returnLeg = new DeliveryFlightPathDTO();
      returnLeg.setDeliveryId(null);
      returnLeg.setFlightPath(backPath.getPath());
      deliveryPaths.add(returnLeg);

      double totalCost = cap.getCostInitial() + cap.getCostFinal() + variableCost;

      RouteResult rr = new RouteResult();
      rr.deliveryPaths = deliveryPaths;
      rr.assignedRecIds = assignedRecIds;
      rr.totalMoves = movesUsed;
      rr.totalCost = totalCost;
      return rr;
  }

  private static class RouteResult {
        List<DeliveryFlightPathDTO> deliveryPaths;
        List<Integer> assignedRecIds;
        int totalMoves;
        double totalCost;
  }

  // -------------------- MEMORY-SAFE MOVES-ONLY SIM --------------------

  protected PathResult computeMovesOnly(LngLatDTO start,
                                      LngLatDTO end,
                                      CapabilityDTO capability,
                                      List<RestrictedAreaDTO> restrictedAreas) {

      PathResult result = new PathResult();
      result.setPath(new ArrayList<>()); // keep empty by contract

      if (start == null || end == null) {
          result.setMoves(0);
          result.setTotalDistance(0.0);
          return result;
      }

      LngLatDTO current = copyPoint(start);
      int moves = 0;
      double totalDistance = 0.0;
      int maxMoves = resolveMaxMoves(capability);

      while (distance(current, end) > CLOSE_EPSILON && moves < maxMoves) {
          LngLatDTO next = stepDirectTowards(current, end);

          // If this step would enter a restricted area, try a detour
          if (isInAnyRestrictedArea(next, restrictedAreas)) {
              LngLatDTO detour = chooseNextStep(current, bearing(current, end), end, restrictedAreas);
              if (detour == null || samePoint(detour, current)) {
                  break; // stuck
              }
              next = detour;
          }

          totalDistance += distance(current, next);
          current = next;
          moves++;
      }

      if (distance(current, end) > CLOSE_EPSILON) {
          moves = maxMoves + 1; // mark infeasible
      }

      result.setMoves(moves);
      result.setTotalDistance(totalDistance);
      return result;
  }

  // -------------------- FULL PATH COMPUTATION --------------------

  protected PathResult computePath(LngLatDTO start,
                                   LngLatDTO end,
                                   CapabilityDTO capability,
                                   List<RestrictedAreaDTO> restrictedAreas) {

      PathResult result = new PathResult();
      List<LngLatDTO> path = new ArrayList<>();

      if (start == null || end == null) {
          result.setPath(path);
          result.setMoves(0);
          result.setTotalDistance(0.0);
          return result;
      }

      PathResult astar = computePathAStar(start, end, capability, restrictedAreas);
      if (astar != null) {
          return astar;
      }
      
       // Fallback: step until within CLOSE_EPSILON without snapping exactly onto target
       LngLatDTO current = copyPoint(start);
       path.add(copyPoint(current));

       int maxMoves = resolveMaxMoves(capability);
       int moves = 0;
       double totalDist = 0.0;

       while (distance(current, end) > CLOSE_EPSILON && moves < maxMoves) {
           LngLatDTO next = stepDirectTowards(current, end);
           if (isInAnyRestrictedArea(next, restrictedAreas)) {
               LngLatDTO detour = chooseNextStep(current, bearing(current, end), end, restrictedAreas);
               if (detour == null || samePoint(detour, current)) break;
               next = detour;
           }
           totalDist += distance(current, next);
           path.add(next);
           current = next;
           moves++;
       }

       result.setPath(path);
       result.setMoves(moves);
       result.setTotalDistance(totalDist);
       return result;
   }

  private PathResult computePathAStar(LngLatDTO start,
                                      LngLatDTO end,
                                      CapabilityDTO capability,
                                      List<RestrictedAreaDTO> restrictedAreas) {

      int maxMoves = resolveMaxMoves(capability);
      String startKey = key(start);
     
      PriorityQueue<Node> open = new PriorityQueue<>((a, b) -> Double.compare(a.fScore, b.fScore));
      Map<String, Node> nodes = new HashMap<>();
      Map<String, String> cameFrom = new HashMap<>();
      Set<String> closed = new HashSet<>();

      Node startNode = new Node(start, 0, heuristic(start, end));
      nodes.put(startKey, startNode);
      open.add(startNode);

      int expansions = 0;
      int maxExpansions = 50000;

      while (!open.isEmpty() && expansions < maxExpansions) {
          Node current = open.poll();
          String curKey = key(current.point);
          if (closed.contains(curKey)) continue;
          closed.add(curKey);

          if (distance(current.point, end) <= CLOSE_EPSILON) {
              return buildPathFromCameFrom(cameFrom, current.point, startKey, end);
          }

          expansions++;

          for (LngLatDTO neighbor : neighbors(current.point, end, restrictedAreas)) {
              String nKey = key(neighbor);
              if (closed.contains(nKey)) continue;

              int tentativeG = current.gScore + 1;
              if (tentativeG > maxMoves) continue;

              Node existing = nodes.get(nKey);
              if (existing == null || tentativeG < existing.gScore) {
                  Node n = new Node(neighbor, tentativeG, heuristic(neighbor, end));
                  nodes.put(nKey, n);
                  cameFrom.put(nKey, curKey);
                  open.add(n);
              }
          }
      }
      return null;
  }



  private List<LngLatDTO> neighbors(LngLatDTO point, LngLatDTO target, List<RestrictedAreaDTO> restrictedAreas) {
      double[] offsets = {
              0, 22.5, 45, 67.5, 90, 112.5, 135, 157.5,
              180, 202.5, 225, 247.5, 270, 292.5, 315, 337.5
      };

      List<LngLatDTO> res = new ArrayList<>();

      for (double off : offsets) {
          LngLatDTO nxt = nextPosition(point, off);
          if (isInAnyRestrictedArea(nxt, restrictedAreas)) continue;
          res.add(nxt);
      }
      return res;
  }

  private double heuristic(LngLatDTO from, LngLatDTO to) {
      double dist = distance(from, to);
      return dist / STEP_SIZE;
  }

  private PathResult buildPathFromCameFrom(Map<String, String> cameFrom,
                                           LngLatDTO goalPoint,
                                           String startKey,
                                           LngLatDTO end) {
      List<LngLatDTO> rev = new ArrayList<>();
      String curKey = key(goalPoint);
      rev.add(copyPoint(goalPoint));

      while (cameFrom.containsKey(curKey)) {
          curKey = cameFrom.get(curKey);
          // find point for key? we stored key only; rebuild from string
          rev.add(pointFromKey(curKey));
          if (curKey.equals(startKey)) break;
      }

      List<LngLatDTO> path = new ArrayList<>();
      for (int i = rev.size() - 1; i >= 0; i--) {
          path.add(rev.get(i));
      }

      PathResult pr = new PathResult();
      pr.setPath(path);
      pr.setMoves(Math.max(0, path.size() - 1));

      double totalDist = 0.0;
      for (int i = 1; i < path.size(); i++) {
          totalDist += distance(path.get(i - 1), path.get(i));
      }
      pr.setTotalDistance(totalDist);
      return pr;
  }

   private String key(LngLatDTO p) {
       // Preserve full precision to avoid rounding drift
       return p.getLng() + ":" + p.getLat();
   }

  private LngLatDTO pointFromKey(String key) {
      String[] parts = key.split(":");
      if (parts.length != 2) return new LngLatDTO(0.0, 0.0);
      double lng = Double.parseDouble(parts[0]);
      double lat = Double.parseDouble(parts[1]);
      return new LngLatDTO(lng, lat);
  }

  private static class Node {
      LngLatDTO point;
      int gScore;
      double fScore;

      Node(LngLatDTO point, int gScore, double hScore) {
          this.point = point;
          this.gScore = gScore;
          this.fScore = gScore + hScore;
      }
  }

  /**
   * Progress-aware steering.
   * - Snap angles to multiples of 22.5°
   * - Prefer moves that reduce distance to target
   * - Avoid restricted areas
   */
  private LngLatDTO chooseNextStep(LngLatDTO current,
                                  double angleToTarget,
                                  LngLatDTO end,
                                  List<RestrictedAreaDTO> restrictedAreas) {

      double curDist = distance(current, end);

      double[] offsets = {
              0, 22.5, -22.5, 45, -45, 67.5, -67.5,
              90, -90, 112.5, -112.5, 135, -135, 157.5, -157.5, 180
      };

      LngLatDTO bestFallback = null;
      double bestFallbackDist = Double.MAX_VALUE;

      for (double off : offsets) {

          double candidateAngle = snapAngle(angleToTarget + off);
          LngLatDTO candidate = nextPosition(current, candidateAngle);

          if (isInAnyRestrictedArea(candidate, restrictedAreas)) continue;

          double candDist = distance(candidate, end);

          // take an improving move immediately
          if (candDist < curDist - 1e-12) {
              return candidate;
          }

          // keep best legal fallback
          if (candDist < bestFallbackDist) {
              bestFallbackDist = candDist;
              bestFallback = candidate;
          }
      }

      return bestFallback;
  }

  // -------------------- RESTRICTED AREAS --------------------

  private boolean isInAnyRestrictedArea(LngLatDTO p, List<RestrictedAreaDTO> areas) {
      if (p == null || areas == null) return false;




      for (RestrictedAreaDTO area : areas) {
          List<LngLatDTO> vertices = area.getVertices();
          if (vertices == null || vertices.size() < 3) continue;
          if (isPointInPolygon(p, vertices)) return true;
      }
      return false;
  }




  private boolean isPointInPolygon(LngLatDTO p, List<LngLatDTO> vertices) {
      boolean inside = false;
      int n = vertices.size();




      for (int i = 0, j = n - 1; i < n; j = i++) {
          LngLatDTO vi = vertices.get(i);
          LngLatDTO vj = vertices.get(j);




          boolean intersect =
                  ((vi.getLat() > p.getLat()) != (vj.getLat() > p.getLat())) &&
                          (p.getLng() < (vj.getLng() - vi.getLng()) *
                                  (p.getLat() - vi.getLat()) /
                                  (vj.getLat() - vi.getLat()) + vi.getLng());




          if (intersect) inside = !inside;
      }
      return inside;
  }




  // -------------------- GEOMETRY / MOVEMENT --------------------




  private double distance(LngLatDTO a, LngLatDTO b) {
      double dx = a.getLng() - b.getLng();
      double dy = a.getLat() - b.getLat();
      return Math.sqrt(dx * dx + dy * dy);
  }




  private double bearing(LngLatDTO from, LngLatDTO to) {
      double dx = to.getLng() - from.getLng();
      double dy = to.getLat() - from.getLat();
      double angleRad = Math.atan2(dy, dx);
      double angleDeg = Math.toDegrees(angleRad);
      return normalizeAngle(angleDeg);
  }




  private double normalizeAngle(double angleDeg) {
      double a = angleDeg % 360.0;
      if (a < 0) a += 360.0;
      return a;
  }




  /** snap to nearest multiple of 22.5 degrees */
  private double snapAngle(double angleDeg) {
      double snapped = Math.round(angleDeg / 22.5) * 22.5;
      return normalizeAngle(snapped);
  }




  /** Single straight-line step of exactly STEP_SIZE toward the target (unless already colocated). */
  private LngLatDTO stepDirectTowards(LngLatDTO from, LngLatDTO to) {
      double dist = distance(from, to);
      if (dist < 1e-12) {
          return copyPoint(to);
      }
      double scale = STEP_SIZE / dist;
      double dx = (to.getLng() - from.getLng()) * scale;
      double dy = (to.getLat() - from.getLat()) * scale;




      LngLatDTO next = new LngLatDTO();
      next.setLng(from.getLng() + dx);
      next.setLat(from.getLat() + dy);
      return next;
  }




  private int resolveMaxMoves(CapabilityDTO capability) {
      int capMoves = (capability == null || capability.getMaxMoves() <= 0)
              ? 5000
              : capability.getMaxMoves();
      return Math.min(capMoves, MAX_MOVE_CAP);
  }




  private LngLatDTO nextPosition(LngLatDTO start, double angleDeg) {
      double angleRad = Math.toRadians(angleDeg);
      double dx = STEP_SIZE * Math.cos(angleRad);
      double dy = STEP_SIZE * Math.sin(angleRad);




      LngLatDTO next = new LngLatDTO();
      next.setLng(start.getLng() + dx);
      next.setLat(start.getLat() + dy);
      return next;
  }




  private boolean samePoint(LngLatDTO a, LngLatDTO b) {
      if (a == null || b == null) return false;
      return Double.compare(a.getLng(), b.getLng()) == 0 &&
              Double.compare(a.getLat(), b.getLat()) == 0;
  }




  private LngLatDTO copyPoint(LngLatDTO p) {
      LngLatDTO copy = new LngLatDTO();
      copy.setLng(p.getLng());
      copy.setLat(p.getLat());
      return copy;
  }




  // -------------------- MATCHING / COST / AVAILABILITY --------------------




  private boolean matchesDispatch(DroneDTO drone,
                                  MedDispatchRecDTO rec,
                                  List<AvailabilitySlotDTO> slots,
                                  LngLatDTO dispatchPoint,
                                  LngLatDTO startPoint) {




      CapabilityDTO cap = drone.getCapability();
      RequirementsDTO req = rec.getRequirements();
      if (cap == null) return false;


      if (req == null) {
          return isDroneAvailableAt(rec, slots);
      }

      if (req.getCapacity() != null && cap.getCapacity() < req.getCapacity()) {
          return false;
      }

      boolean needCooling = Boolean.TRUE.equals(req.getCooling());
      boolean needHeating = Boolean.TRUE.equals(req.getHeating());

      if (needCooling && !cap.isCooling()) return false;
      if (needHeating && !cap.isHeating()) return false;
      if (needCooling && needHeating && (!cap.isCooling() || !cap.isHeating())) return false;

      return isDroneAvailableAt(rec, slots);
  }

  private double euclideanDistance(LngLatDTO a, LngLatDTO b) {
      double dx = a.getLng() - b.getLng();
      double dy = a.getLat() - b.getLat();
      return Math.sqrt(dx * dx + dy * dy);
  }

  private boolean isDroneAvailableAt(MedDispatchRecDTO rec,
                                     List<AvailabilitySlotDTO> slots) {

      if (slots == null || slots.isEmpty()) return false;
      if (rec.getDate() == null || rec.getTime() == null) return false;




      DayOfWeek day = rec.getDate().getDayOfWeek();
      LocalTime time = rec.getTime();




      for (AvailabilitySlotDTO slot : slots) {
          if (slot.getDayOfWeek() == day &&
                  !time.isBefore(slot.getFrom()) &&
                  !time.isAfter(slot.getUntil())) {
              return true;
          }
      }
      return false;
  }

  // -------------------- MAP BUILDERS --------------------

  private Map<Integer, List<AvailabilitySlotDTO>> buildAvailabilityMap(
          List<ServicePointAvailabilityDTO> availabilityData) {

      Map<Integer, List<AvailabilitySlotDTO>> map = new HashMap<>();
      if (availabilityData == null) return map;

      for (ServicePointAvailabilityDTO sp : availabilityData) {
          if (sp.getDrones() == null) continue;
          for (DroneAvailabilityDTO d : sp.getDrones()) {
              if (d.getId() == null || d.getAvailability() == null) continue;
              try {
                  int id = Integer.parseInt(d.getId());
                  map.merge(id,
                          new ArrayList<>(d.getAvailability()),
                          (oldList, newList) -> {
                              oldList.addAll(newList);
                              return oldList;
                          });
              } catch (NumberFormatException ignored) {}
          }
      }
      return map;
  }

  private Map<Integer, LngLatDTO> buildDispatchPointMap(
          List<ServicePointAvailabilityDTO> availabilityData,
          Map<Integer, LngLatDTO> servicePointLocationById) {




      Map<Integer, LngLatDTO> map = new HashMap<>();
      if (availabilityData == null) return map;

      for (ServicePointAvailabilityDTO sp : availabilityData) {
          LngLatDTO spLoc = servicePointLocationById.get(sp.getServicePointId());
          if (spLoc == null || sp.getDrones() == null) continue;

          for (DroneAvailabilityDTO d : sp.getDrones()) {
              if (d.getId() == null) continue;
              try {
                  int droneId = Integer.parseInt(d.getId());
                  map.put(droneId, spLoc);
              } catch (NumberFormatException ignored) {}
          }
      }
      return map;
  }
 }
