import javafx.util.Pair;

import java.util.*;

class Player {

    private static ArrayList<String> command = new ArrayList<>();

    private static ArrayList[] minionTargetR = new ArrayList[5];
    private static ArrayList[] minionTargetC = new ArrayList[5];
    private static boolean[] commandDone = new boolean[5];
    private static String[] role = new String[5];
    private static final String UNASSIGNED = "UNASSIGNED", FLAG = "FLAG", ATTACK = "ATTACK", DEFENSE = "DEFENSE", COIN = "COIN", USELESS = "USELESS";


    private static int height;
    private static int width;
    private static String map[][];
    private static int myFlagBaseR;
    private static int myFlagBaseC;
    private static int opponentFlagBaseR;
    private static int opponentFlagBaseC;
    private static String fireName;
    private static int firePrice;
    private static int fireDamage;
    private static String freezeName;
    private static int freezePrice;
    private static int freezeDamage;
    private static String mineName;
    private static int minePrice;
    private static int mineDamage;
    private static int myScore;
    private static int opponentScore;
    private static int myFlagPosR;
    private static int myFlagPosC;
    private static int myFlagCarrier;
    private static int opponentFlagPosR;
    private static int opponentFlagPosC;
    private static int opponentFlagCarrier;
    private static int myAliveMinionCnt;
    private static int[] myAliveMinionId;
    private static Integer[] myAliveMinionPosR = new Integer[5];
    private static Integer[] myAliveMinionPosC = new Integer[5];
    private static Integer[] myAliveMinionHealth = new Integer[5];
    private static Integer[] myAliveMinionTimeout = new Integer[5];
    private static int visibleMinionCnt;
    private static int[] visibleMinionId;
    private static Integer[] visibleMinionPosR = new Integer[5];
    private static Integer[] visibleMinionPosC = new Integer[5];
    private static Integer[] visibleMinionHealth = new Integer[5];
    private static Integer[] visibleMinionTimeout = new Integer[5];
    private static int visibleCoinCnt;
    private static Integer[] visibleCoinPosR;
    private static Integer[] visibleCoinPosC;

    private static void takeInitialInputs(Scanner in) {
        height = in.nextInt();
        width = in.nextInt();

        map = new String[height][width];
        for (int i = 0; i < height; i++) {
            String row = in.next();
            String[] rowValues = row.split("");
            for (int j = 0; j < width; j++) {
                map[i][j] = rowValues[j];
            }
        }

        // print map
//        for (int i = 0; i < height; i++) {
//            for (int j = 0; j < width; j++) {
//                // System.err.print(map[i][j]);
//            }
//            // System.err.println();
//        }

        myFlagBaseR = in.nextInt();
        myFlagBaseC = in.nextInt();
        opponentFlagBaseR = in.nextInt();
        opponentFlagBaseC = in.nextInt();
        fireName = in.next();
        firePrice = in.nextInt();
        fireDamage = in.nextInt();
        freezeName = in.next();
        freezePrice = in.nextInt();
        freezeDamage = in.nextInt();
        mineName = in.next();
        minePrice = in.nextInt();
        mineDamage = in.nextInt();
    }

    private static void takeInputs(Scanner in) {
        myScore = in.nextInt();
        opponentScore = in.nextInt();
        myFlagPosR = in.nextInt();
        myFlagPosC = in.nextInt();
        myFlagCarrier = in.nextInt();
        opponentFlagPosR = in.nextInt();
        opponentFlagPosC = in.nextInt();
        opponentFlagCarrier = in.nextInt();
        myAliveMinionCnt = in.nextInt();
        myAliveMinionId = new int[myAliveMinionCnt];
        myAliveMinionPosR = new Integer[5];
        myAliveMinionPosC = new Integer[5];
        myAliveMinionHealth = new Integer[5];
        myAliveMinionTimeout = new Integer[5];
        for (int i = 0; i < myAliveMinionCnt; i++) {
            int id = in.nextInt();
            myAliveMinionId[i] = id;
            myAliveMinionPosR[id]  = in.nextInt();
            myAliveMinionPosC[id]  = in.nextInt();
            myAliveMinionHealth[id]  = in.nextInt();
            myAliveMinionTimeout[id]  = in.nextInt();
        }
        visibleMinionCnt = in.nextInt();
        visibleMinionId = new int[visibleMinionCnt];
        visibleMinionPosR = new Integer[5];
        visibleMinionPosC = new Integer[5];
        visibleMinionHealth = new Integer[5];
        visibleMinionTimeout = new Integer[5];
        for (int i = 0; i < visibleMinionCnt; i++) {
            int id = in.nextInt();
            visibleMinionId[i] = id;
            visibleMinionPosR[id]  = in.nextInt();
            visibleMinionPosC[id]  = in.nextInt();
            visibleMinionHealth[id]  = in.nextInt();
            visibleMinionTimeout[id]  = in.nextInt();
        }
        visibleCoinCnt = in.nextInt();
        visibleCoinPosR = new Integer[visibleCoinCnt];
        visibleCoinPosC = new Integer[visibleCoinCnt];
        for (int i = 0; i < visibleCoinCnt; i++) {
            visibleCoinPosR[i] = in.nextInt();
            visibleCoinPosC[i] = in.nextInt();
        }
    }

    private static String makeCommand(int cnt) {
        String out = "";
        for (int i = 0; i < cnt; i++) {
            out += command.get(i);
            if (i != cnt-1) out += " | ";
        }
        return out;
    }

    private static boolean directPathExists(int pos1R, int pos1C, int pos2R, int pos2C) {
        if (pos1R == pos2R) {
            int smaller, larger;
            if (pos1C < pos2C) {smaller = pos1C; larger = pos2C;}
            else {smaller = pos2C; larger = pos1C;}

            while (smaller <= larger) {
                if (Objects.equals(map[pos1R][smaller], "#")) {
                    return false;
                }
                smaller++;
            }
            return true;
        } else if (pos1C == pos2C) {
            int smaller, larger;
            if (pos1R < pos2R) {smaller = pos1R; larger = pos2R;}
            else {smaller = pos2R; larger = pos1R;}

            while (smaller <= larger) {
                if (Objects.equals(map[smaller][pos1C], "#")) {
                    return false;
                }
                smaller++;
            }
            return true;
        }

        return false;
    }

    private static ArrayList<Integer> affectMyMinion(int id) {
        ArrayList<Integer> affected = new ArrayList<>();
        for (int i : myAliveMinionId) {
            if (i == id) continue;
            // System.err.println("affectMyMinion " + id + " " + i);
            if (directPathExists(myAliveMinionPosR[id], myAliveMinionPosC[id], myAliveMinionPosR[i], myAliveMinionPosC[i])) {
                // System.err.println("affectMyMinion " + id + " " + i);
                affected.add(i);
            }
        }

        return affected;
    }

    private static ArrayList<Integer> affectOpponentMinion(int id) {
        ArrayList<Integer> affected = new ArrayList<>();
        for (int i : visibleMinionId) {
            if (directPathExists(myAliveMinionPosR[id], myAliveMinionPosC[id], visibleMinionPosR[i], visibleMinionPosC[i])) {
                // System.err.println("affectOpponentMinion " + id + " " + i);
                affected.add(i);
            }
        }

        return affected;
    }

    private static boolean shouldFire(int id, int threshold) {
        if (affectOpponentMinion(id).contains(myFlagCarrier) && myScore >= firePrice) {
            // System.err.println("shouldFire " + id + " " + affectMyMinion(id).size() + " " + affectOpponentMinion(id).size());
            return true;
        } else if (id != opponentFlagCarrier && visibleMinionCnt != 0 /*&& myScore-opponentScore >= firePrice*/ && myScore >= threshold) {
            if (!affectMyMinion(id).contains(opponentFlagCarrier) && affectMyMinion(id).size() < affectOpponentMinion(id).size()) {
                // System.err.println("shouldFire " + id + " " + affectMyMinion(id).size() + " " + affectOpponentMinion(id).size());
                return true;
            }
        }

        return false;
    }

    private static boolean shouldFreeze(int id, int threshold) {
        if (affectOpponentMinion(id).contains(myFlagCarrier) && myScore >= freezePrice) {
            return true;
        } else if (id != opponentFlagCarrier && visibleMinionCnt != 0 /*&& myScore-opponentScore >= freezePrice*/ && myScore >= threshold) {
            if (!affectMyMinion(id).contains(opponentFlagCarrier) && affectMyMinion(id).size() == 0 && affectOpponentMinion(id).size() > 0) {
                return true;
            }
        }

        return false;
    }

    private static Integer getR(int id) {
        if (minionTargetR[id].size() > 0) {
            return (Integer) minionTargetR[id].get(minionTargetR[id].size()-1);
        } else {
            return -1;
        }
    }

    private static Integer getC(int id) {
        if (minionTargetC[id].size() > 0) {
            return (Integer) minionTargetC[id].get(minionTargetC[id].size()-1);
        } else {
            return -1;
        }
    }

    private static int adjOfMyBase() {
        if (myFlagBaseC < width/2) {
            return myFlagBaseC + 1;
        } else {
            return myFlagBaseC - 1;
        }
    }

    private static boolean assignRole(int id, String assignedRole) {
        role[id] = assignedRole;

        if (assignedRole == USELESS) {
            return true;
        } else if (assignedRole == UNASSIGNED) {
            // do nothing
            commandDone[id] = true;
            return true;

        } else if (assignedRole == FLAG) {
            if (opponentFlagCarrier == id) {
                minionTargetR[id].add(myFlagBaseR);
                minionTargetC[id].add(myFlagBaseC);
            } else {
                minionTargetR[id].add(opponentFlagPosR);
                minionTargetC[id].add(opponentFlagPosC);
            }
            commandDone[id] = false;
            return true;

        } else if (assignedRole == ATTACK) {
            // find nearest opponent minion
            for (int i : visibleMinionId) {
                if (i == opponentFlagCarrier) {
                    // check if anyone else has gone there
                    boolean shouldGoForIt = true;
                    for (int j : myAliveMinionId) {
                        if (j != id && getR(j) == visibleMinionPosR[opponentFlagCarrier] && getC(j) == visibleMinionPosC[opponentFlagCarrier]) {
                            shouldGoForIt = false;
                            break;
                        }
                    }
                    if (shouldGoForIt) {
                        minionTargetR[id].add(visibleMinionPosR[opponentFlagCarrier]);
                        minionTargetC[id].add(visibleMinionPosC[opponentFlagCarrier]);
                        commandDone[id] = false;
                        return true;
                    }
                }

                boolean shouldGoForIt = true;
                for (int j : myAliveMinionId) {
                    if (j != id && getR(j) == visibleMinionPosR[i] && getC(j) == visibleMinionPosC[i]) {
                        shouldGoForIt = false;
                        break;
                    }
                }
                if (shouldGoForIt) {
                    minionTargetR[id].add(visibleMinionPosR[i]);
                    minionTargetC[id].add(visibleMinionPosC[i]);
                    commandDone[id] = false;
                    return true;
                }
            }
            // no opponent for attack found
            assignRole(id, COIN);
            assignRole(id, UNASSIGNED);
            return false;

        } else if (assignedRole == DEFENSE) {
            // go back to own flag base
            if (myFlagPosR == myFlagBaseR && myFlagPosC == myFlagBaseC) {
                minionTargetR[id].add(myFlagPosR);
                minionTargetC[id].add(adjOfMyBase());
                commandDone[id] = false;
            } else {
                minionTargetR[id].add(myFlagPosR);
                minionTargetC[id].add(myFlagPosC);
                commandDone[id] = false;
            }
            return true;


        } else if (assignedRole == COIN) {
            List<Pair<Integer, Integer>> visibleCoinPos = new ArrayList<>();
            for (int i = 0; i < visibleCoinCnt; i++) {
                visibleCoinPos.add(new Pair<>(visibleCoinPosR[i], visibleCoinPosC[i]));
            }
            Collections.shuffle(visibleCoinPos);

            // check for nearest coin
            for (Pair<Integer, Integer> coinPos : visibleCoinPos) {
                int r = coinPos.getKey();
                int c = coinPos.getValue();

                boolean shouldGoForIt = true;
                for (int j : myAliveMinionId) {
                    if (j != id && getR(j) == r && getC(j) == c) {
                        shouldGoForIt = false;
                        break;
                    }
                }
//                if (shouldGoForIt && directPathExists(myAliveMinionPosR[id], myAliveMinionPosC[id], r, c)) {
//                    minionTargetR[id].add(r);
//                    minionTargetC[id].add(c);
//                    commandDone[id] = false;
//                    return true;
//                }
                if (shouldGoForIt /*&& Math.abs(myAliveMinionPosR[id] - r) <= 12 && Math.abs(myAliveMinionPosC[id] - c) <= 5*/) {
                    minionTargetR[id].add(r);
                    minionTargetC[id].add(c);
                    commandDone[id] = false;
                    return true;
                }
            }

            // no coin found
            minionTargetR[id].add(opponentFlagPosR);
            minionTargetC[id].add(opponentFlagPosC);
            assignRole(id, UNASSIGNED);
            return false;
        }

        // System.err.println("Unknown role: " + assignedRole);
        return false;
    }

    private static int adjOfOpponentBase() {
        if (opponentFlagBaseC < width/2) {
            return opponentFlagBaseC + 1;
        } else {
            return opponentFlagBaseC - 1;
        }
    }

    private static boolean shouldMineOpponentsBase(int id) {
        if (id == opponentFlagCarrier && myScore >= minePrice && myFlagCarrier != -1) {
            if (Math.abs(myAliveMinionPosC[id] - opponentFlagBaseC) == 1 && Math.abs(myAliveMinionPosR[id] - opponentFlagBaseR) == 1) {
                return true;
            }
            if (Math.abs(myAliveMinionPosC[id] - opponentFlagPosC) == 2 && Math.abs(myAliveMinionPosR[id] - opponentFlagBaseR) == 0) {
                return true;
            }
        }
        return false;
    }

    private static boolean shouldMineMyFlagPos(int id) {
        return myScore > minePrice && ((Math.abs(myAliveMinionPosC[id] - myFlagPosC) == 1 && Math.abs(myAliveMinionPosR[id] - myFlagPosR) == 0)
                || (Math.abs(myAliveMinionPosR[id] - myFlagPosR) == 1 && Math.abs(myAliveMinionPosC[id] - myFlagPosC) == 0));
    }

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        takeInitialInputs(in);
        takeInputs(in);

        for (int i=0; i<5; i++) {
            minionTargetR[i] = new ArrayList<Integer>();
            minionTargetC[i] = new ArrayList<Integer>();
            commandDone[i] = false;
            role[i] = UNASSIGNED;
        }

        // check for useless
        // later


        ////////////// game loop
        while (true) {
            int flagCollectorMinionCnt = 0, coinCollectorMinionCnt = 0, defenseMinionCnt = 0, attackMinionCnt = 0;

            ////////// strategic factors
//            if (opponentFlagCarrier == -1) {
                // loop through all minions and check who have gone to the locations they were assigned to
                for (int id : myAliveMinionId) {
                    if (Objects.equals(role[id], COIN)) {
                        if (!Arrays.asList(visibleCoinPosR).contains(getR(id)) || !Arrays.asList(visibleCoinPosC).contains(getC(id))) {
                            assignRole(id, UNASSIGNED);
                        }
                    }
                    if (/*!Objects.equals(role[id], DEFENSE) && !Objects.equals(role[id], ATTACK) &&*/ myAliveMinionPosR[id] == getR(id) && myAliveMinionPosC[id] == getC(id)) {
                        assignRole(id, UNASSIGNED);
                    }
                }

                defenseMinionCnt = 1;
                if (myFlagCarrier != -1) attackMinionCnt = 1;
                coinCollectorMinionCnt = myAliveMinionCnt - defenseMinionCnt - attackMinionCnt;

                // update role count
                for (int id : myAliveMinionId) {
                    if (role[id] == DEFENSE) {
                        defenseMinionCnt--;
                    } else if (role[id] == ATTACK) {
                        attackMinionCnt--;
                    } else if (role[id] == COIN) {
                        coinCollectorMinionCnt--;
                    }
                }

                // determining who to send for flag and who for coins
                List<Pair<Integer, Integer>> distanceFromMyFlag = new ArrayList<>();
                for (int id : myAliveMinionId) {
                    distanceFromMyFlag.add(new Pair(Math.abs(myAliveMinionPosC[id] - myFlagPosC), id));
                }
                Collections.sort(distanceFromMyFlag, (Comparator<Pair<Integer, Integer>>) (o1, o2) -> {
                    if (o1.getKey().compareTo(o2.getKey()) == 0) {
                        return o1.getValue().compareTo(o2.getValue());
                    } else {
                        return o1.getKey().compareTo(o2.getKey());
                    }
                });

                for (Pair<Integer, Integer> pair : distanceFromMyFlag) {
                    int id = pair.getValue();
                    if (role[id] == UNASSIGNED) {
                        if (defenseMinionCnt > 0) {
                            assignRole(id, DEFENSE);
                            defenseMinionCnt--;
                        } else if (attackMinionCnt > 0) {
                            assignRole(id, ATTACK);
                            attackMinionCnt--;
                        } else {
                            assignRole(id, COIN);
                            coinCollectorMinionCnt--;
                        }
                    }
                }

                command.clear();
                for (int id : myAliveMinionId) {
                    /*if (shouldMineOpponentsBase(id)) {
                        command.add("MINE " + id + " " + opponentFlagBaseR + " " + adjOfOpponentBase());
                    } else*/ if (shouldFire(id, firePrice)) {
                        command.add("FIRE " + id);
//                    } else if (shouldFreeze(id, 20)) {
//                        command.add("FREEZE " + id);
//                    } else if (shouldMineMyFlagPos(id)) {
//                        command.add("MINE " + id + " " + myFlagPosR + " " + myFlagPosC);
//                        minedMyBase++;
//                        assignRole(id, UNASSIGNED);
                    } else {
                        command.add("MOVE " + id + " " + getR(id) + " " + getC(id));
                    }
                }

                ////////////// strategic factor
//            } else {
//                // loop through all minions and check who have gone to the locations they were assigned to
//                for (int id : myAliveMinionId) {
//                    if (role[id] == FLAG && id != opponentFlagCarrier) {
//                        assignRole(id, UNASSIGNED);
//                    }
//                    if (Objects.equals(role[id], COIN)) {
//                        if (!Arrays.asList(visibleCoinPosR).contains(getR(id)) || !Arrays.asList(visibleCoinPosC).contains(getC(id))) {
//                            assignRole(id, UNASSIGNED);
//                        }
//                    }
//                    if (Objects.equals(role[id], ATTACK)) {
//                        assignRole(id, ATTACK);
//                    } else if (myAliveMinionPosR[id] == getR(id) && myAliveMinionPosC[id] == getC(id)) {
//                        assignRole(id, UNASSIGNED);
//                    }
//                }
//
//                flagCollectorMinionCnt = 1;
//                attackMinionCnt = 1;
//                coinCollectorMinionCnt = myAliveMinionCnt - flagCollectorMinionCnt - attackMinionCnt;
//
//                // update role count
//                for (int id : myAliveMinionId) {
//                    if (role[id] == FLAG) {
//                        flagCollectorMinionCnt--;
//                    } else if (role[id] == COIN) {
//                        coinCollectorMinionCnt--;
////                    } else if (role[id] == DEFENSE) {
////                        defenseMinionCnt--;
//                    } else if (role[id] == ATTACK) {
//                        attackMinionCnt--;
//                    }
//                }
//
//                // just to check whom to send for flag
//                List<Pair<Integer, Integer>> distanceFromFlag = new ArrayList<>();
//                for (int id : myAliveMinionId) {
//                    distanceFromFlag.add(new Pair(Math.abs(myAliveMinionPosC[id] - opponentFlagPosC), id));
//                }
//                Collections.sort(distanceFromFlag, (Comparator<Pair<Integer, Integer>>) (o1, o2) -> {
//                    return o1.getKey().compareTo(o2.getKey());
//                });
//
//                for (Pair<Integer, Integer> pair : distanceFromFlag) {
//                    int id = pair.getValue();
//                    if (role[id] == UNASSIGNED) {
//                        if (flagCollectorMinionCnt > 0) {
//                            assignRole(id, FLAG);
//                            flagCollectorMinionCnt--;
//                        } else if (attackMinionCnt > 0) {
//                            assignRole(id, ATTACK);
//                            attackMinionCnt--;
//                        } else {
//                            assignRole(id, COIN);
//                            coinCollectorMinionCnt--;
//                        }
//                    }
//                }
//
//                command.clear();
//                for (int id : myAliveMinionId) {
//                    if (shouldMineOpponentsBase(id)) {
//                        command.add("MINE " + id + " " + opponentFlagBaseR + " " + opponentFlagBaseC);
//                    } else if (shouldFreeze(id, freezePrice)) {
//                        command.add("FREEZE " + id);
//                    } else if (shouldFire(id, firePrice)) {
//                        command.add("FIRE " + id);
////                    } else if (shouldMineMyFlagPos(id)) {
////                        command.add("MINE " + id + " " + myFlagPosR + " " + myFlagPosC);
//                    } else {
//                        command.add("MOVE " + id + " " + getR(id) + " " + getC(id));
//                    }
//                }
//            }




            //////////////////////////////////////////
            System.out.println(makeCommand(myAliveMinionCnt));
            //////////////////////////////////////////
            takeInputs(in);
            //////////////////////////////////////////
        }
    }
}