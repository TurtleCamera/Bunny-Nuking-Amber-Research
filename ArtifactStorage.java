import java.util.Random;

// Class to represent the artifact inventory
public class ArtifactStorage {
    // Keeps track of the number of artifacts we've made (just for the purpose of IDs)
    public int nextArtifactID = 0;   

    // Stores MAX_ARTIFACTS randomly generated artifacts for each slot. Add
    // an extra slot for the purpose of a newly-generated artifact.
    public final int MAX_ARTIFACTS = 5;    // Set to 5 for large iterations, 20 for small iterations

    // Max number of slots for artifacts
    public final int MAX_SLOTS = 5;

    // Storage arrays for each type of artifact
    public Artifact [] flowers;
    public Artifact [] feathers;
    public Artifact [] sands;
    public Artifact [] goblets;
    public Artifact [] circlets;

    // Set effect that this storage is based on
    public ArtifactSetEffect setEffect;

    public ArtifactStorage(ArtifactSetEffect set) {
        // All we have to do is just initialize the storage arrays for each
        // artifact slot (+ 1, so we can generate a new artifact to check)
        flowers = new Artifact[MAX_ARTIFACTS + 1];
        feathers = new Artifact[MAX_ARTIFACTS + 1];
        sands = new Artifact[MAX_ARTIFACTS + 1];
        goblets = new Artifact[MAX_ARTIFACTS + 1];
        circlets = new Artifact[MAX_ARTIFACTS + 1];

        // Set the set effect of this artifact
        setEffect = set;

        // Unseeded RNG
        Artifact.rng = new Random();
    }

    // Generates new artifacts for each slot
    public void generateNewArtifacts() {
        // Find the empty slots in each artifact array in the storage
        int [] emptyslots = findEmptySlots();

        // Generate 1 new artifact for each slot 
        // (should always have an empty slot)
        flowers[emptyslots[0]] = new Artifact(0, nextArtifactID ++);
        feathers[emptyslots[1]] = new Artifact(1, nextArtifactID ++);
        sands[emptyslots[2]] = new Artifact(2, nextArtifactID ++);
        goblets[emptyslots[3]] = new Artifact(3, nextArtifactID ++);
        circlets[emptyslots[4]] = new Artifact(4, nextArtifactID ++);

        // Max these artifacts outs
        flowers[emptyslots[0]].maxArtifact();
        feathers[emptyslots[1]].maxArtifact();
        sands[emptyslots[2]].maxArtifact();
        goblets[emptyslots[3]].maxArtifact();
        circlets[emptyslots[4]].maxArtifact();
    }

    // Applies the set effect to Amber
    public void applySetEffect(Amber amber) {
        // Determine which type of set effect this is
        if(setEffect == ArtifactSetEffect.TwoCWoFTwoGF) {
            amber.atkPercent += 18;
            amber.elementalDmgBonus += 15;
        }
        else if(setEffect == ArtifactSetEffect.FourCWoF) {
            // Assuming one stack for now
            // 2-piece effect
            amber.elementalDmgBonus += 15; 

            // 4-piece effect
            amber.elementalDmgBonus += 7.5;
            amber.reactionBonuses.add(15.0);

            // // 2 stacks effect
            // amber.elementalDmgBonus += 7.5;
        }
        else if(setEffect == ArtifactSetEffect.FourFP) {
            // Assuming one stack for now
            // 2-piece effect
            amber.dmgMultipliers.add(20.0);

            // 4-piece effect
            amber.em += 150; 
            amber.atkPercent += 10;

            // // 2 stacks effect
            // amber.em += 150; 
            // amber.atkPercent += 10;

        }
        else if(setEffect == ArtifactSetEffect.FourWT) {
            // 2-piece effect
            amber.em += 80; 

            // 4-piece effect
            amber.dmgMultipliers.add(35.0);
        }
        else if(setEffect == ArtifactSetEffect.FourBS) {
            // 2-piece effect
            // Does not help Amber

            // 4-piece effect
            amber.critRate += 40;
        }
        else if(setEffect == ArtifactSetEffect.TwoGFTwoSR) {
            // Two 2-piece effects
            amber.atkPercent += 18;
            amber.atkPercent += 18;
        }
        else if(setEffect == ArtifactSetEffect.TwoGFTwoWT) {
            // Assuming one stack for now
            // 2-piece effects
            amber.atkPercent += 18;
            amber.em += 80;
        }
        else {
            // This set effect doesn't exist
            System.out.println("Set effect " + setEffect + " does not exist.");
            System.exit(-1);
        }
    }

    // Returns an array containing indices with empty slots
    public int [] findEmptySlots() {
        int [] emptySlots = new int[MAX_SLOTS];

        // Find the next empty slot for each artifact array
        emptySlots[0] = findEmptyIndex(flowers);
        emptySlots[1] = findEmptyIndex(feathers);
        emptySlots[2] = findEmptyIndex(sands);
        emptySlots[3] = findEmptyIndex(goblets);
        emptySlots[4] = findEmptyIndex(circlets);

        return emptySlots;
    }

    // Given an array, find the first empty index
    public int findEmptyIndex(Artifact [] artifacts) {
        // Find the first empty slot in artifacts
        for(int i = 0; i < artifacts.length; i ++) {
            if(artifacts[i] == null) {
                return i;
            }
        }

        // Exit if we didn't find a slot
        System.out.println("Failed to find an empty slot in the artifact array.");
        System.exit(-1);

        return -1;
    }

    // For each artifact in the 5 slots, check if this new damage
    // is the new best damage for that artifact. If so, then replace
    // the previous best damage with this new damage.
    public void checkNewBestDamage(double damage, int flowerIndex, int featherIndex,
                                   int sandsIndex, int gobletIndex, int circletIndex) {
        // Check flower's best damage
        if(flowers[flowerIndex].bestDamage < damage) {
            flowers[flowerIndex].bestDamage = damage;
        }

        // Check feather's best damage
        if(feathers[featherIndex].bestDamage < damage) {
            feathers[featherIndex].bestDamage = damage;
        }

        // Check sands's best damage
        if(sands[sandsIndex].bestDamage < damage) {
            sands[sandsIndex].bestDamage = damage;
        }

        // Check goblet's best damage
        if(goblets[gobletIndex].bestDamage < damage) {
            goblets[gobletIndex].bestDamage = damage;
        }

        // Check circlet's best damage
        if(circlets[circletIndex].bestDamage < damage) {
            circlets[circletIndex].bestDamage = damage;
        }
    }

    // Assumes that the best damage value has already been calculated
    // for all the artifacts in this storage. Removes the artifact
    // that has the lowest damage value. Only does this if we have
    // more than MAX_ARTIFACTS in an artifact array.
    public void removeWorstArtifacts() {
        // Check each array
        processArray(flowers);
        processArray(feathers);
        processArray(sands);
        processArray(goblets);
        processArray(circlets);
    }

    // Helper function for above that actually does the artifact removal process
    public void processArray(Artifact [] artifacts) {
        // Variables used to determine the worst artifact
        int count = 0;  // Number of artifacts in the array
        double lowestDamage = Integer.MAX_VALUE;   // Lowest damage in the array
        int lowestIndex = 0;    // Index of the artifact with the lowest damage output in the array

        // Check the flowers array
        for(int i = 0; i < artifacts.length; i ++) {
            // Increment count if there is an artifact in this slot
            if(artifacts[i] != null) {
                count ++;

                // Is this the new lowest damage artifact?
                if(artifacts[i].bestDamage < lowestDamage) {
                    lowestIndex = i;
                    lowestDamage = artifacts[i].bestDamage;
                }
            }
        }

        // If there are more than MAX_ARTIFACTS in the array, remove
        // the worst artifact from the array.
        if(MAX_ARTIFACTS < count) {
            artifacts[lowestIndex] = null;
        }
    }

    // Find the best performing artifacts and store them in Amber
    public void findBestArtifacts(Amber amber) {
        amber.bestFlower = bestArtifact(flowers);
        amber.bestFeather = bestArtifact(feathers);
        amber.bestSands = bestArtifact(sands);
        amber.bestGoblet = bestArtifact(goblets);
        amber.bestCirclet = bestArtifact(circlets);

        // If the damage output value in one of these artifacts is
        // different, then something is wrong
        if((amber.bestFlower.bestDamage != amber.bestFeather.bestDamage) ||
           (amber.bestFlower.bestDamage != amber.bestSands.bestDamage) ||
           (amber.bestFlower.bestDamage != amber.bestGoblet.bestDamage) ||
           (amber.bestFlower.bestDamage != amber.bestCirclet.bestDamage)) {
            // There's a problem
            System.out.println("One of the best damage values doesn't match with the rest of the artifacts.");
            System.out.println("Flower: " + amber.bestFlower.bestDamage);
            System.out.println("Feather: " + amber.bestFeather.bestDamage);
            System.out.println("Sands: " + amber.bestSands.bestDamage);
            System.out.println("Goblet: " + amber.bestGoblet.bestDamage);
            System.out.println("Circlet: " + amber.bestCirclet.bestDamage);
            System.exit(-1);
        }
    }

    // Helper function for above. Takes in an array of artifacts and
    // finds the one with the highest damage output
    public Artifact bestArtifact(Artifact [] artifacts) {
        // Variables used to determine the best artifact in this array
        double highestDamage = -1;
        int highestIndex = 0;

        for(int i = 0; i < artifacts.length; i ++) {
            if(artifacts[i] != null) {
                if(artifacts[i].bestDamage > highestDamage) {
                    highestIndex = i;
                    highestDamage = artifacts[i].bestDamage;
                }
            }
        }

        return artifacts[highestIndex];
    }
}
