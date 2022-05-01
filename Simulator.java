import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.Random;

// Note: this program doesn't take into account enemy defense, but it's
// simply a multiplier over incoming damage, so this shouldn't change
// the best-in-slot artifact set and weapon.
public class Simulator {
    // Number of iterations that we want to run for this simulator
    public static final int NUM_ITERATIONS = 100000;   
            
    // Seed for all the artifact storages
    public static int SEED = 1;

    // Stores all the weapons
    public static final Weapon [] weapons = { Weapon.Aqua_Simulacra,
                                              Weapon.Polar_Star, Weapon.Skyward_Harp,
                                              Weapon.Thundering_Pulse, Weapon.Prototype_Crescent,
                                              Weapon.Amos_Bow, Weapon.The_Stringless_R5 };
    
    // The file we write to
    public static PrintWriter out;

    // The number of experiments
    public static int NUM_EXPERIMENTS = 2;

    public static void main(String [] args) throws IOException {
        for(int fileNumber = 0; fileNumber < NUM_EXPERIMENTS; fileNumber ++) {
            // Create the file we want to write to
            // out = new PrintWriter(new BufferedWriter(new FileWriter("Test outputs\\50,000 iterations\\Yelan Bow\\Teammate buffs\\Bennett and Sucrose\\output" + fileNumber + ".txt")));
            out = new PrintWriter(new BufferedWriter(new FileWriter("Test outputs\\100,000 iterations\\Compare Blizzard Strayer\\output" + fileNumber + ".txt")));

            // Print out the seed
            // SEED = fileNumber + (int) (Math.random() * 10000);
            SEED = fileNumber;
            out.println("Seed: " + SEED);
            out.println();

            // Run tests on every weapon
            for(int i = 0; i < weapons.length; i ++) {
                // Print to console
                System.out.println("================================================================================");
                System.out.println("Running tests for " + weapons[i]);
                System.out.println("================================================================================");

                // Print to file
                out.println("================================================================================");
                out.println("================================================================================");
                out.println("================================================================================");
                out.println("Running tests for " + weapons[i]);
                out.println("================================================================================");
                out.println("================================================================================");
                out.println("================================================================================");
                out.println();
                runExperiment(weapons[i]);
            }

            // Close the output file
            out.close();
        }

        // Print to console
        System.out.println("================================================================================");
        System.out.println("                                Tests completed!");
        System.out.println("================================================================================");
    }

    // Runs the experiment
    public static void runExperiment(Weapon weaponType) {
        // NOTE: THE ARTIFACT STORAGE SYSTEM SEEDS THE ARTIFACT RNG STATIC VARIABLE
        //       UPON CALLING THE CONSTRUCTOR, SO WE CAN'T CREATE ALL THE ARTIFACT
        //       STORAGES AT ONCE. I'M TOO DEEP INTO THE PROJECT TO CHANGE THAT
        //       WITHOUT MAJOR REFACTORING AT THIS POINT.
        // New Amber object
        Amber amber = new Amber(weaponType);

        // // Create new artifact storages and test them
        // // 2-piece Crimson Witch of Flames and 2-piece Gladiator's Finale
        // ArtifactStorage storageTwoCWoFTwoGF = new ArtifactStorage(ArtifactSetEffect.TwoCWoFTwoGF);
        // Artifact.rng = new Random(SEED);
        // printHeader("2-piece Crimson Witch of Flames and 2-piece Gladiator's Finale");
        // optimizeStorage(amber, storageTwoCWoFTwoGF);
        // int bestTwoCWoFTwoGF = amber.damageOutput;

        // Create new artifact storages and test them
        // 4-piece Crimson Witch of Flames
        ArtifactStorage storageFourCWoF = new ArtifactStorage(ArtifactSetEffect.FourCWoF);
        Artifact.rng = new Random(SEED);
        printHeader("4-piece Crimson Witch of Flames");
        optimizeStorage(amber, storageFourCWoF);
        int bestFourCWoF = amber.damageOutput;

        // 4-piece Fleeting Promise
        ArtifactStorage storageFourFP = new ArtifactStorage(ArtifactSetEffect.FourFP);
        Artifact.rng = new Random(SEED);
        printHeader("4-piece Fleeting Promise");
        optimizeStorage(amber, storageFourFP);
        int bestFourFP = amber.damageOutput;

        // 4-piece Blizzard Strayer
        ArtifactStorage storageFourBS = new ArtifactStorage(ArtifactSetEffect.FourBS);
        Artifact.rng = new Random(SEED);
        printHeader("4-piece Blizzard Strayer");
        optimizeStorage(amber, storageFourBS);
        int bestFourBS = amber.damageOutput;

        // // 4-piece Wanderer's Troupe
        // ArtifactStorage storageFourWT = new ArtifactStorage(ArtifactSetEffect.FourWT);
        // Artifact.rng = new Random(SEED);
        // printHeader("4-piece Wanderer's Troupe");
        // optimizeStorage(amber, storageFourWT);
        // int bestFourWT = amber.damageOutput;

        // Print out the final damage values of each artifact set
        out.println("================================================================================");
        out.println("Best damage values for each artifact set:");
        out.println("================================================================================");
        out.println("Best 4-piece Fleeting Promise damage: " + bestFourFP);
        out.println("Best 4-piece Crimson Witch of Flames damage: " + bestFourCWoF);
        out.println("Best 4-piece Blizzard Strayer: " + bestFourBS);
        // out.println("Best 2-piece Crimson Witch of Flames and 2-piece Gladiator's Finale damage: " + bestTwoCWoFTwoGF);
        // out.println("Best 4-piece Wanderer's Troupe damage: " + bestFourWT);
        out.println("\n\n\n\n");
    }

    // Prints out a "header" for an artifact storage
    public static void printHeader(String header) {
        out.println("================================================================================");
        out.println(header);
        out.println("================================================================================");

        // Print to console
        System.out.println("Computing for " + header);
    }

    // Optimizes an artifact storage. Specifically, it will keep rolling RNG and keeping
    // the best artifacts. Then, it will use an Amber object to compute the best damage.
    public static void optimizeStorage(Amber amber, ArtifactStorage storage) {
        // Analyze this artifact storage
        analyzeArtifactStorage(storage, amber);

        // Get Amber's best artifacts
        storage.findBestArtifacts(amber);

        // Reset her stats
        amber.resetStats();
        amber.applyArtifactStats(amber.bestFlower, amber.bestFeather, amber.bestSands, amber.bestGoblet, amber.bestCirclet);
        storage.applySetEffect(amber);

        // Calculate the final damage and store it in Amber's stats
        // amber.computeChargedShot();
        amber.computeManualBunny();
        // amber.computeUlt();

        // Print out the stuff
        amber.printInfo(out);
    }

    // Will go through every combination of artifacts to see which one outputs the best
    // damage output. The algorithm will set the best average damage output for each
    // artifact in this storage and use that to determine which artifact to remove from
    // the storage system if there are more than MAX_ARTIFACTS in one of the storage arrays.
    // Will use an Amber object to compute the average damage.
    public static void analyzeArtifactStorage(ArtifactStorage storage, Amber amber) {
        // Run the simulation
        for(int i = 0; i < NUM_ITERATIONS; i ++) {
            // Generate new artifacts for each slot
            storage.generateNewArtifacts();

            // Loop through every possible combination of artifacts in our artifact storage.
            for(int flowerIndex = 0; flowerIndex < storage.flowers.length; flowerIndex ++) {
                // Make sure this artifact slot isn't empty
                if(storage.flowers[flowerIndex] == null) {
                    continue;
                }

                for(int featherIndex = 0; featherIndex < storage.feathers.length; featherIndex ++) {
                    // Make sure this artifact slot isn't empty
                    if(storage.feathers[featherIndex] == null) {
                        continue;
                    }

                    for(int sandsIndex = 0; sandsIndex < storage.sands.length; sandsIndex ++) {
                        // Make sure this artifact slot isn't empty
                        if(storage.sands[sandsIndex] == null) {
                            continue;
                        }

                        for(int gobletIndex = 0; gobletIndex < storage.goblets.length; gobletIndex ++) {
                            // Make sure this artifact slot isn't empty
                            if(storage.goblets[gobletIndex] == null) {
                                continue;
                            }

                            for(int circletIndex = 0; circletIndex < storage.circlets.length; circletIndex ++) {
                                // Make sure this artifact slot isn't empty
                                if(storage.circlets[circletIndex] == null) {
                                    continue;
                                }

                                // Extract the artifacts
                                Artifact flower = storage.flowers[flowerIndex];
                                Artifact feather = storage.feathers[featherIndex];
                                Artifact sands = storage.sands[sandsIndex];
                                Artifact goblet = storage.goblets[gobletIndex];
                                Artifact circlet = storage.circlets[circletIndex];

                                // Reset Amber's stats and apply these artifact stats to her
                                amber.resetStats();
                                amber.applyArtifactStats(flower, feather, sands, goblet, circlet);
                                storage.applySetEffect(amber);

                                // Compute barron bunny damage
                                // int damageOutput = amber.computeChargedShot();
                                int damageOutput = amber.computeManualBunny();
                                // int damageOutput = amber.computeUlt();

                                // Replace old damage values in these 5 artifacts if this new
                                // damage output is better than what they currently have
                                storage.checkNewBestDamage(damageOutput, flowerIndex, featherIndex,
                                                           sandsIndex, gobletIndex, circletIndex);
                            }
                        }
                    }
                }
            }
                                
            // If the arrays are full, remove the worst-performing artifact
            storage.removeWorstArtifacts();
        }
    }
}