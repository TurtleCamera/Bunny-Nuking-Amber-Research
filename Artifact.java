import java.io.PrintWriter;
import java.util.Random;

// Class to represent an artifact
class Artifact {
    // Best damage value (used to determine which artifact to remove)
    public double bestDamage;

    // Max number of slots for artifacts
    public final int MAX_SLOTS = 5;

    // Number of substats
    public final int MAX_SUBSTATS = 4;

    // Main stat
    public StatType mainStatType;
    public double mainStatValue;

    // Sub-stat types and values
    public StatType [] subStatType;
    public double [] subStatValues;

    // Number of sub-stat rolls remaining
    public int remainingRolls;

    // ID represents the id-th artifact
    public int id;

    // RNG that we use (shared across all artifacts)
    public static Random rng = new Random();

    // Constructor for the Artifact
    public Artifact(int slot, int id) {
        // Initialize best damage to -1 for now
        bestDamage = -1;

        // Initialize the substat arrays
        subStatType = new StatType[MAX_SUBSTATS];
        subStatValues = new double[MAX_SUBSTATS];

        // Generate main stat
        generateMainStat(slot);
        
        // Generate substats
        generateSubStats();

        // Store the ID of this artifact
        this.id = id;
    }

    // Even though they're defined in the enum, ignore the sub-stats
    // that aren't useful.
    private void generateMainStat(int slot) {
        // If the slot value is invalid, then just randomly choose a slot value
        if(slot < 0 || slot >= MAX_SLOTS) {
            System.out.println("Invalid slot detected. Randomly generating a new slot value.");
            slot = rng.nextInt(MAX_SLOTS);
        }

        // Main stat type and value depends on the slot value
        if(slot == 0) {
            // Flower
            mainStatType = StatType.HP;
            mainStatValue = 4780;
        }
        else if(slot == 1) {
            // Feather
            mainStatType = StatType.ATK;
            mainStatValue = 311;
        }
        else if(slot == 2) {
            // Sands (randomly generate main stat). Only use ATK% or Elemental Mastery
            // Randomly generated value
            double chooseValue = rng.nextInt(2);
            if(chooseValue == 0) {
                mainStatType = StatType.ATK_Percent;
                mainStatValue = 46.6;
            }
            else {
                mainStatType = StatType.Elemental_Mastery;
                mainStatValue = 186.5;
            }
        }
        else if(slot == 3) {
            // Goblet (randomly generate main stat). Only use ATK%, Elemental DMG Bonus,
            // or Elemental Mastery
            // Randomly generated value
            double chooseValue = rng.nextInt(3);
            if(chooseValue == 0) {
                mainStatType = StatType.Elemental_DMG;
                mainStatValue = 46.6;
            }
            else if(chooseValue == 1) {
                mainStatType = StatType.ATK_Percent;
                mainStatValue = 46.6;
            }
            else {
                mainStatType = StatType.Elemental_Mastery;
                mainStatValue = 186.5;
            }
        }
        else if(slot == 4) {
            // Circlet (randomly generate main stat). Only use ATK%, CRIT Rate, CRIT DMG,
            // or Elemental Mastery
            // Randomly generated value
            double chooseValue = rng.nextInt(2);
            if(chooseValue == 0) {
                mainStatType = StatType.CRIT_Rate;
                mainStatValue = 31.1;
            }
            else if(chooseValue == 1) {
                mainStatType = StatType.CRIT_DMG;
                mainStatValue = 62.2;
            }
            // else if(chooseValue == 2) {
            //     mainStatType = StatType.ATK_Percent;
            //     mainStatValue = 46.6;
            // }
            // else {
            //     mainStatType = StatType.Elemental_Mastery;
            //     mainStatValue = 186.5;
            // }
        }
    }

    // For substats, however, we want to be more realistic by adding some
    // useless stats
    public void generateSubStats() {
        // If the main stat type doesn't exist, then set one for a random slot
        if(mainStatType == null) {
            // Main stat doesn't exist when generating substats, so generating
            // one now
            generateMainStat(rng.nextInt(MAX_SLOTS));
        }

        // Either 3 or 4 initial substats (recorded as remaining rolls left).
        // Specifically, if we have 3 initial stats, then still genreate a 4th
        // stat, but set it to 4 remaining rolls instead of 5.
        // remainingRolls = rng.nextInt(2) + 4;
        // We should probably stop adduming 4 initial substats because it creates
        // busted stats a lot of the time
        remainingRolls = 4;

        // Keep looping until we get 4 substats that don't match the main stat
        int subStatIndex = 0;
        while(subStatIndex < 4) {
            // Randomly generate a substat type
            int chooseSubStat = rng.nextInt(10);

            if(chooseSubStat == 0) {
                subStatType[subStatIndex] = StatType.HP;
            }
            else if(chooseSubStat == 1) {
                subStatType[subStatIndex] = StatType.ATK;
            }
            else if(chooseSubStat == 2) {
                subStatType[subStatIndex] = StatType.DEF;
            }
            else if(chooseSubStat == 3) {
                subStatType[subStatIndex] = StatType.HP_Percent;
            }
            else if(chooseSubStat == 4) {
                subStatType[subStatIndex] = StatType.ATK_Percent;
            }
            else if(chooseSubStat == 5) {
                subStatType[subStatIndex] = StatType.DEF_Percent;
            }
            else if(chooseSubStat == 6) {
                subStatType[subStatIndex] = StatType.Elemental_Mastery;
            }
            else if(chooseSubStat == 7) {
                subStatType[subStatIndex] = StatType.Energy_Recharge;
            }
            else if(chooseSubStat == 8) {
                subStatType[subStatIndex] = StatType.CRIT_Rate;
            }
            else if(chooseSubStat == 9) {
                subStatType[subStatIndex] = StatType.CRIT_DMG;
            }

            // If this stat matches the main stat, then reroll
            if(subStatType[subStatIndex] == mainStatType) {
                // Don't advance the index
                continue;
            }
            else {
                // Make sure we don't duplicate a substat
                boolean duplicateFound = false;
                for(int i = 0; i < subStatIndex; i ++) {
                    if((subStatType[i] != null) && (subStatType[subStatIndex] == subStatType[i])) {
                        // Duplicate stat found, so set the boolean value to true
                        duplicateFound = true;
                        break;
                    }
                }

                // Did we find a duplicate stat?
                if(duplicateFound) {
                    // Reroll
                    continue;
                }

                // Otherwise, generate the initial substat value for this
                // substat type
                upgradeSubStat(subStatIndex);
            }

            // Increment the substat index
            subStatIndex ++;
        }
    }

    // Maxes out an artifact (note, the code doesn't simulate the
    // actual main stat upgrade process, but it just randomly chooses)
    // substats based on the number of remaining rolls left.
    public void maxArtifact() {
        while(remainingRolls > 0) {
            randomlyUpgradeSubStat();
            remainingRolls --;
        }
    }

    // Randomly upgrades a substat
    public void randomlyUpgradeSubStat() {
        // Randomly choose a substat index
        int chooseSubStatIndex = 3;

        // "Upgrade" that substat
        upgradeSubStat(chooseSubStatIndex);
    }

    // "Upgrades" a substat based on the type and slot given
    public void upgradeSubStat(int subStatIndex) {
        // RNG value used to determine the upgrade value
        int chooseValue = rng.nextInt(4);

        if(subStatType[subStatIndex] == StatType.HP) {
            if(chooseValue == 0) {
                subStatValues[subStatIndex] += 209.13;
            }
            else if(chooseValue == 1) {
                subStatValues[subStatIndex] += 239.00;
            }
            else if(chooseValue == 2) {
                subStatValues[subStatIndex] += 268.88;
            }
            else {
                subStatValues[subStatIndex] += 298.75;
            }
        }
        else if(subStatType[subStatIndex] == StatType.ATK) {
            if(chooseValue == 0) {
                subStatValues[subStatIndex] += 13.62;
            }
            else if(chooseValue == 1) {
                subStatValues[subStatIndex] += 15.56;
            }
            else if(chooseValue == 2) {
                subStatValues[subStatIndex] += 17.51;
            }
            else {
                subStatValues[subStatIndex] += 19.45;
            }
        }
        else if(subStatType[subStatIndex] == StatType.DEF) {
            if(chooseValue == 0) {
                subStatValues[subStatIndex] += 16.20;
            }
            else if(chooseValue == 1) {
                subStatValues[subStatIndex] += 18.52;
            }
            else if(chooseValue == 2) {
                subStatValues[subStatIndex] += 20.83;
            }
            else {
                subStatValues[subStatIndex] += 23.15;
            }
        }
        else if(subStatType[subStatIndex] == StatType.HP_Percent) {
            if(chooseValue == 0) {
                subStatValues[subStatIndex] += 4.08;
            }
            else if(chooseValue == 1) {
                subStatValues[subStatIndex] += 4.66;
            }
            else if(chooseValue == 2) {
                subStatValues[subStatIndex] += 5.25;
            }
            else {
                subStatValues[subStatIndex] += 5.83;
            }
        }
        else if(subStatType[subStatIndex] == StatType.ATK_Percent) {
            if(chooseValue == 0) {
                subStatValues[subStatIndex] += 4.08;
            }
            else if(chooseValue == 1) {
                subStatValues[subStatIndex] += 4.66;
            }
            else if(chooseValue == 2) {
                subStatValues[subStatIndex] += 5.25;
            }
            else {
                subStatValues[subStatIndex] += 5.83;
            }
        }
        else if(subStatType[subStatIndex] == StatType.DEF_Percent) {
            if(chooseValue == 0) {
                subStatValues[subStatIndex] += 5.10;
            }
            else if(chooseValue == 1) {
                subStatValues[subStatIndex] += 5.83;
            }
            else if(chooseValue == 2) {
                subStatValues[subStatIndex] += 6.56;
            }
            else {
                subStatValues[subStatIndex] += 7.29;
            }
        }
        else if(subStatType[subStatIndex] == StatType.Elemental_Mastery) {
            if(chooseValue == 0) {
                subStatValues[subStatIndex] += 16.32;
            }
            else if(chooseValue == 1) {
                subStatValues[subStatIndex] += 18.65;
            }
            else if(chooseValue == 2) {
                subStatValues[subStatIndex] += 20.98;
            }
            else {
                subStatValues[subStatIndex] += 23.31;
            }
        }
        else if(subStatType[subStatIndex] == StatType.Energy_Recharge) {
            if(chooseValue == 0) {
                subStatValues[subStatIndex] += 4.53;
            }
            else if(chooseValue == 1) {
                subStatValues[subStatIndex] += 5.18;
            }
            else if(chooseValue == 2) {
                subStatValues[subStatIndex] += 5.83;
            }
            else {
                subStatValues[subStatIndex] += 6.48;
            }
        }
        else if(subStatType[subStatIndex] == StatType.CRIT_Rate) {
            if(chooseValue == 0) {
                subStatValues[subStatIndex] += 2.72;
            }
            else if(chooseValue == 1) {
                subStatValues[subStatIndex] += 3.11;
            }
            else if(chooseValue == 2) {
                subStatValues[subStatIndex] += 3.50;
            }
            else {
                subStatValues[subStatIndex] += 3.89;
            }
        }
        else if(subStatType[subStatIndex] == StatType.CRIT_DMG) {
            if(chooseValue == 0) {
                subStatValues[subStatIndex] += 5.44;
            }
            else if(chooseValue == 1) {
                subStatValues[subStatIndex] += 6.22;
            }
            else if(chooseValue == 2) {
                subStatValues[subStatIndex] += 6.99;
            }
            else {
                subStatValues[subStatIndex] += 7.77;
            }
        }
    }

    // Just prints out the details about the artifact
    public void printStats(PrintWriter out) {
        out.println("Information for artifact #" + id);

        // Print information about the main stat
        out.println("Main stat " + mainStatType + ": " + mainStatValue + "\n");

        // Print information about the substats
        out.println("Substats");
        for(int i = 0; i < subStatType.length; i ++) {
            out.printf("%s: %.2f\n", subStatType[i], subStatValues[i]);
        }

        out.println("\n");
    }
}