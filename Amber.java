import java.io.PrintWriter;
import java.util.*;

class Amber {
    // Amber's stats
    public double baseATK;
    public double flatATK;
    public double atkPercent;
    public double em;
    public double critRate;
    public double critDmg;
    public double elementalDmgBonus;
    public double level;
    public ArrayList<Double> dmgMultipliers;    // Anything that says "DMG" in-game
    public ArrayList<Double> reactionBonuses;   // Anything that boosts reaction multipliers like melt

    // Multipliers
    public double chargedShotMultiplier;
    public double bunnyMultiplier;
    public double fieryRainMultiplier;

    // Stores the best artifact so far
    public Artifact bestFlower;
    public Artifact bestFeather;
    public Artifact bestSands;
    public Artifact bestGoblet;
    public Artifact bestCirclet;

    // Store Amber's weapon
    public Weapon weapon;
    
    // Stores the average damage computed by one of the damage computation functions
    public int damageOutput;

    // Initialize the starting values for each of these stats and multipliers
    public Amber(Weapon weaponType) {
        // Ability multipliers
        chargedShotMultiplier = 2.232;
        bunnyMultiplier = 2.2176;
        fieryRainMultiplier = 9.0979;

        // Set initial best damage
        damageOutput = -1;

        // Set initial stats
        resetStats(weaponType);

        // Set her weapon
        weapon = weaponType;
    }

    // If no weapon is passed in, then just call resetStats with
    // the weapon stored in this object
    public void resetStats() {
        resetStats(weapon);
    }

    // Resets the stats for Amber (as in "remove" artifacts stats).
    // Used in the constructor too to set initial values
    public void resetStats(Weapon weaponType) {
        // Base values
        baseATK = 223;
        flatATK = 0;
        atkPercent = 0;
        em = 0;
        critRate = 5;
        critDmg = 50;
        elementalDmgBonus = 0;  // Pyro DMG Bonus for Amber
        level = 90;

        // Other multipliers
        dmgMultipliers = new ArrayList<Double>();
        reactionBonuses = new ArrayList<Double>();

        // Extra values
        atkPercent += 24;   // Ascension passive stat
        atkPercent += 15;   // Passive

        // Apply a weapon
        applyWeapon(weaponType);

        // Reset the damage output value
        damageOutput = 0;
    }

    // Applies a weapon's stats to Amber
    public void applyWeapon(Weapon weaponType) {
        // Check the type of weapon applied
        if(weaponType == Weapon.Sharpshooters_Oath) {
            // Sharpshooter's Oath stats
            baseATK += 401;   // Sharpshooter's Oath base ATK
            critDmg += 46.9;  // Sharpshooter's Oath crit damage
    
            // Sharpshooter's Oath 48% weakspot multiplier
            // Commented out because it doesn't affect barron bunny
            // dmgMultipliers.add(48.0);    
        }
        else if(weaponType == Weapon.Prototype_Crescent) {
            // Prototype Crescent stats
            baseATK += 510;     // Prototype Crescent base ATK
            atkPercent += 41.3; // Prototype Crescent ATK%

            // Passive ability
            atkPercent += 72;   // Passive ATK%
        }
        else if(weaponType == Weapon.Skyward_Harp) {
            // Skyward Harp stats
            baseATK += 674;     // Skyward Harp base ATK
            critRate += 22.1;   // Skyward Harp crit rate
            critDmg += 20;      // Skyward Harp crit damage
        }
        else if(weaponType == Weapon.Amos_Bow) {
            // Amos' Bow stats
            baseATK += 608;     // Amos' Bow base ATK
            atkPercent += 49.6; // Amos' Bow ATK%
    
            // Amos' Bow Strong-Willed passive
            // Commented out because it doesn't affect barron bunny
            // int numStacks = 1;
            // dmgMultipliers.add(12.0 + numStacks * 8.0);    
        }
        else if(weaponType == Weapon.Polar_Star) {
            // Polar Star stats
            baseATK += 608;     // Polar Star base ATK
            critRate += 33.1;   // Polar Star crit rate

            // Daylight's Augury
            dmgMultipliers.add(12.0);
            atkPercent += 48;   // Shouldn't be to hard to get due to the rotation
        }
        else if(weaponType == Weapon.Thundering_Pulse) {
            // Thundering Pulse stats
            baseATK += 608;     // Thundering Pulse base ATK
            critDmg += 66.2;    // Thundering Pulse crit damage

            // Rule By Thunder (the rest of the passive only affects normal attack)
            atkPercent += 20;   // ATK% buff
        }
        else if(weaponType == Weapon.The_Stringless_R1) {
            // The Stringless R1 stats
            baseATK += 510;     // The Stringless base ATK
            em += 165;          // The Stringless elemental mastery

            // Arrowless Song
            dmgMultipliers.add(24.0);
        }
        else if(weaponType == Weapon.The_Stringless_R5) {
            // The Stringless R5 stats
            baseATK += 510;     // The Stringless base ATK
            em += 165;          // The Stringless elemental mastery

            // Arrowless Song
            dmgMultipliers.add(48.0);
        }
        else if(weaponType == Weapon.Aqua_Simulacra) {
            // The Aqua Simulacra stats
            baseATK += 542;     // The Aqua Simulacra base ATK
            critDmg += 88.2;    // The Aqua Simulacra crit damage

            // Passive
            dmgMultipliers.add(20.0);
        }
    }

    // Applies the artifact stats to Amber's stats
    public void applyArtifactStats(Artifact flower, Artifact feather,
                                   Artifact sands, Artifact goblet,
                                   Artifact circlet) {
        // Apply main stats first
        applyStat(flower.mainStatType, flower.mainStatValue);
        applyStat(feather.mainStatType, feather.mainStatValue);
        applyStat(sands.mainStatType, sands.mainStatValue);
        applyStat(goblet.mainStatType, goblet.mainStatValue);
        applyStat(circlet.mainStatType, circlet.mainStatValue);

        // Now apply substats
        // Flower
        for(int i = 0; i < flower.subStatType.length; i ++) {
            applyStat(flower.subStatType[i], flower.subStatValues[i]);
        }
        // Feather
        for(int i = 0; i < feather.subStatType.length; i ++) {
            applyStat(feather.subStatType[i], feather.subStatValues[i]);
        }
        // Sands
        for(int i = 0; i < sands.subStatType.length; i ++) {
            applyStat(sands.subStatType[i], sands.subStatValues[i]);
        }
        // Goblet
        for(int i = 0; i < goblet.subStatType.length; i ++) {
            applyStat(goblet.subStatType[i], goblet.subStatValues[i]);
        }
        // Circlet
        for(int i = 0; i < circlet.subStatType.length; i ++) {
            applyStat(circlet.subStatType[i], circlet.subStatValues[i]);
        }
    }

    // Applies a specific stat to Amber. Pretty much a helper function for
    // applyArtifactStats
    public void applyStat(StatType type, double value) {
        // Ignore all the stats that aren't relavent to damage calculations
        if(type == StatType.ATK) {
            flatATK += value;
        }
        else if(type == StatType.ATK_Percent) {
            atkPercent += value;
        }
        else if(type == StatType.Elemental_Mastery) {
            em += value;
        }
        else if(type == StatType.CRIT_Rate) {
            critRate += value;
        }
        else if(type == StatType.CRIT_DMG) {
            critDmg += value;
        }
        else if(type == StatType.Elemental_DMG) {
            elementalDmgBonus += value;
        }
    }

    public int computeChargedShot() {
        // This is the calculator for computing charged shot damage (only for weakspots,
        // so crit rate is ignored). Assumes we melt.
        double totalATK = flatATK + baseATK * (1 + (atkPercent / 100)); // Total ATK

        // Damage bonuses with the word "DMG"
        double totalDmgBonus = 1;
        totalDmgBonus += elementalDmgBonus / 100; // Elemental DMG bonus counts
        for(int i = 0; i < dmgMultipliers.size(); i ++) {
            totalDmgBonus += dmgMultipliers.get(i) / 100;    // Special multipliers count
        }

        // Outgoing damage formula in the damage wiki tab
        double outgoingDmg = totalATK * chargedShotMultiplier * totalDmgBonus;

        // Factor in critical hits
        double critMultiplier = 1 + (critDmg / 100);
        double outgoingDmgCrit = outgoingDmg * critMultiplier;

        // Incoming damage formula
        double defAndResMultiplier = hilichurlDefAndResMultiplier();
        double incomingDmg = outgoingDmgCrit * defAndResMultiplier;

        // Amplifier from melt
        double emBonus = 2.78 * (em / (em + 1400));
        double reactionMultiplier = 2;
        double reactionBonus = 0;
        for(int i = 0; i < reactionBonuses.size(); i ++) {
            reactionBonus += reactionBonuses.get(i) / 100;    // Special multipliers count
        }
        double amplifyingMultiplier = reactionMultiplier * (1 + emBonus + reactionBonus);
        double amplifiedDmg = incomingDmg * amplifyingMultiplier;

        // Set Amber's damage output and return the rounded formula
        damageOutput = (int) (amplifiedDmg);
        return (int) (amplifiedDmg);  
    }

    public int computeManualBunny() {
        // IMPORTANT: THE DOUBLE DAMAGE FOR BARRON BUNNY ISN'T ACTUALLY A X3 MULTIPLIER
        // OVER FINAL DAMAGE. IT'S INSTEAD A DAMAGE MULTIPLIER, SO IT'S NOT ACTUALLY
        // X3.
        dmgMultipliers.add(200.0);

        // This is the calculator for computing charged shot damage (only for weakspots,
        // so crit rate is ignored). Assumes we melt.
        double totalATK = flatATK + baseATK * (1 + (atkPercent / 100)); // Total ATK

        // Damage bonuses with the word "DMG"
        double totalDmgBonus = 1;
        totalDmgBonus += elementalDmgBonus / 100; // Elemental DMG bonus counts
        for(int i = 0; i < dmgMultipliers.size(); i ++) {
            totalDmgBonus += dmgMultipliers.get(i) / 100;    // Special multipliers count
        }

        // Outgoing damage formula in the damage wiki tab
        double outgoingDmg = totalATK * bunnyMultiplier * totalDmgBonus;

        // Factor in critical hits
        double critDmgMultiplier = critDmg / 100;
        double critRateMultiplier = critRate / 100;
        // Don't let effective crit rate go over 100%
        if(critRateMultiplier > 1) {
            critRateMultiplier = 1;
        }
        double avgOutgoingDmg = outgoingDmg * (1 + critRateMultiplier * critDmgMultiplier);

        // Incoming damage formula
        double defAndResMultiplier = hilichurlDefAndResMultiplier();
        double incomingDmg = avgOutgoingDmg * defAndResMultiplier;

        // Amplifier from melt
        double totalEm = em;
        double emBonus = 2.78 * (totalEm / (totalEm + 1400));
        double reactionMultiplier = 2;
        double reactionBonus = 0;
        for(int i = 0; i < reactionBonuses.size(); i ++) {
            reactionBonus += reactionBonuses.get(i) / 100;    // Special multipliers count
        }
        double amplifyingMultiplier = reactionMultiplier * (1 + emBonus + reactionBonus);
        double amplifiedDmg = incomingDmg * amplifyingMultiplier;

        // Set Amber's damage output and return the rounded formula
        damageOutput += (int) (amplifiedDmg);
        return (int) (amplifiedDmg);  
    }

    public int computeUlt() {
        // This is the calculator for computing charged shot damage (only for weakspots,
        // so crit rate is ignored). Assumes we melt.
        double totalATK = flatATK + baseATK * (1 + (atkPercent / 100)); // Total ATK

        // Damage bonuses with the word "DMG"
        double totalDmgBonus = 1;
        totalDmgBonus += elementalDmgBonus / 100; // Elemental DMG bonus counts
        // Comment this out for now because I don't think there's anything that contributes
        // to Amber's ult "DMG" multiplier
        // for(int i = 0; i < dmgMultipliers.size(); i ++) {
        //     totalDmgBonus += dmgMultipliers.get(i) / 100;    // Special multipliers count
        // }

        // Outgoing damage formula in the damage wiki tab
        double outgoingDmg = totalATK * fieryRainMultiplier * totalDmgBonus;

        // Factor in critical hits
        double critDmgMultiplier = critDmg / 100;
        double critRateMultiplier = critRate / 100;
        critRateMultiplier +=  + 0.1;   // Amber's passive
        // Don't let effective crit rate go over 100%
        if(critRateMultiplier > 1) {
            critRateMultiplier = 1;
        }
        double avgOutgoingDmg = outgoingDmg * (1 + critRateMultiplier * critDmgMultiplier);

        // Incoming damage formula
        double defAndResMultiplier = hilichurlDefAndResMultiplier();
        double incomingDmg = avgOutgoingDmg * defAndResMultiplier;

        // Amplifier from melt not applied because it's unreasonable to melt every hit

        // Set Amber's damage output and return the rounded formula
        damageOutput += (int) (incomingDmg);
        return (int) (incomingDmg);  
    }

    public double hilichurlDefAndResMultiplier() {
        // Experimental: take into account a hilichurl's defense in calculations
        double enemyDef = 87 * 5 + 500;
        double defMitigation = (1.0 - (enemyDef)/(enemyDef + (5.0 * level) + 500.0));

        // Resistance to elements
        double enemyRes = 1 - 0.1;

        return defMitigation * enemyRes;
    }

    // Applies Bennett's ult
    public double applyBennettUlt() {
        // It's just some flat ATK
        int prototypeRancourBaseATK = 565;
        int bennettBaseATK = 191;
        return (prototypeRancourBaseATK + bennettBaseATK) * 0.952;

        // Kind of lazy to figure out how to better do this, but, assuming
        // Bennett has Noblesse Oblige, just add 0.2 to the totalATK
        // formula in the damage calculation formulas above
    }

    // Applies Diona's EM buff
    public double applyDionaUlt() {
        // It's just 200 EM
        return 200;
    }

    // Applies Sucrose's EM buff
    public double applySucroseBuffs() {
        int flatEM = 50;
        // int mappaMareEM = 110;
        int sacrificialFragmentsEM = 0;  // 221
        int sucroseEM = 639;
        return 0.2 * (sucroseEM + sacrificialFragmentsEM) + flatEM;

        // Kind of lazy to figure out how to better do this, but, assuming
        // Bennett has Noblesse Oblige, just add 0.48 to the totalATK
        // formula in the damage calculation formulas above
    }

    // Apply Kazuha's Pyro DMG buff
    public double applyKazuhaBuffs() {
        int ironStingEM = 165;
        int kazuhaEM = 561;

        return 0.04 * (ironStingEM + kazuhaEM) / 100;
    }

    // Prints out information regarding Amber
    public void printInfo(PrintWriter out) {
        out.println("Information about Amber");
        
        // Damage output
        out.println("Damage output for the build below: " + damageOutput + "\n");

        // Print information about her stats
        out.println("Relevant stats");
        // ATK
        double totalATK = flatATK + baseATK * (1 + (atkPercent / 100));
        out.printf("ATK: %.2f\n", totalATK);
        // EM
        out.printf("Elemental Mastery: %.2f\n", em);
        // Crit Rate
        out.printf("CRIT Rate: %.2f\n", critRate);
        // Crit DMG
        out.printf("CRIT DMG: %.2f\n", critDmg);
        // Pyro DMG Bonus
        out.printf("Pyro (Elemental) DMG Bonus: %.2f\n", elementalDmgBonus);
        // "Damage bonuses"
        double totalDmgBonus = 0;
        for(int i = 0; i < dmgMultipliers.size(); i ++) {
            totalDmgBonus += dmgMultipliers.get(i);
        }
        out.printf("\"Damage bonuses\": %.2f\n", totalDmgBonus);
        // "Reaction bonuses"
        double reactionBonus = 0;
        for(int i = 0; i < reactionBonuses.size(); i ++) {
            reactionBonus += reactionBonuses.get(i);
        }
        out.printf("\"Reaction bonuses\": %.2f\n\n", reactionBonus);

        // Print out information about the artifacts themselves
        bestFlower.printStats(out);
        bestFeather.printStats(out);
        bestSands.printStats(out);
        bestGoblet.printStats(out);
        bestCirclet.printStats(out);

        out.println();
    }
}