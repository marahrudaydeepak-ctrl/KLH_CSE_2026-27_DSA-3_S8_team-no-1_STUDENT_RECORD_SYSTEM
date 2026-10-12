package algorithms.randomized;

import model.AlgorithmResult;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Manual implementation of the Miller-Rabin Probabilistic Primality Test.
 * Application: Evaluates cryptographic student ID tokens and key security.
 * Error probability bounded by (1/4)^k where k is the number of randomized witness rounds.
 */
public class MillerRabin {

    public static class PrimalityResult {
        public long number;
        public int rounds;
        public boolean isProbablePrime;
        public double errorProbability;
        public List<String> roundDetails;

        public PrimalityResult(long number, int rounds) {
            this.number = number;
            this.rounds = rounds;
            this.roundDetails = new ArrayList<>();
        }
    }

    public static PrimalityResult testPrimality(long n, int kRounds) {
        PrimalityResult result = new PrimalityResult(n, kRounds);

        if (n <= 1) {
            result.isProbablePrime = false;
            return result;
        }
        if (n <= 3) {
            result.isProbablePrime = true;
            return result;
        }
        if (n % 2 == 0) {
            result.isProbablePrime = false;
            result.roundDetails.add("Divisible by 2 (Even composite number).");
            return result;
        }

        // Write n - 1 as d * 2^s by factoring powers of 2 from n - 1
        long d = n - 1;
        int s = 0;
        while (d % 2 == 0) {
            d /= 2;
            s++;
        }

        Random rand = new Random();
        BigInteger bigN = BigInteger.valueOf(n);
        BigInteger bigD = BigInteger.valueOf(d);

        for (int i = 0; i < kRounds; i++) {
            // Pick a random witness base a in [2, n - 2]
            long a = 2 + (Math.abs(rand.nextLong()) % (n - 3));
            BigInteger bigA = BigInteger.valueOf(a);

            // Compute x = a^d mod n
            BigInteger x = bigA.modPow(bigD, bigN);

            if (x.equals(BigInteger.ONE) || x.equals(BigInteger.valueOf(n - 1))) {
                result.roundDetails.add(String.format("Round %d: Base a=%d passed Fermat base witness condition.", i + 1, a));
                continue;
            }

            boolean composite = true;
            for (int r = 1; r < s; r++) {
                x = x.modPow(BigInteger.valueOf(2), bigN);
                if (x.equals(BigInteger.valueOf(n - 1))) {
                    composite = false;
                    result.roundDetails.add(String.format("Round %d: Base a=%d passed square witness condition at power r=%d.", i + 1, a, r));
                    break;
                }
            }

            if (composite) {
                result.isProbablePrime = false;
                result.errorProbability = 0.0;
                result.roundDetails.add(String.format("Round %d: Base a=%d proved n=%d is COMPOSITE.", i + 1, a, n));
                return result;
            }
        }

        result.isProbablePrime = true;
        result.errorProbability = Math.pow(0.25, kRounds);
        return result;
    }

    public static AlgorithmResult executeMillerRabin(long candidateNumber, int kRounds) {
        long start = System.nanoTime();
        PrimalityResult res = testPrimality(candidateNumber, kRounds);
        long duration = System.nanoTime() - start;

        List<String> steps = new ArrayList<>();
        steps.add(String.format("Candidate Numeric Token: %,d | Random Witness Rounds: %d", candidateNumber, kRounds));
        steps.addAll(res.roundDetails);
        steps.add(String.format("Conclusion: %,d is %s with error probability bounded by (1/4)^%d = %.8f%%.",
                candidateNumber, res.isProbablePrime ? "PROBABLE PRIME" : "DEFINITELY COMPOSITE",
                kRounds, res.errorProbability * 100.0));

        AlgorithmResult result = new AlgorithmResult();
        result.setAlgorithmName("Miller-Rabin Primality Test");
        result.setCategory("Randomized Algorithms (Monte Carlo)");
        result.setPurpose("Probabilistic verification of student encryption/hashing tokens");
        result.setInputSize((int) (Math.log(candidateNumber) / Math.log(2))); // Bit length
        result.setDataStructureUsed("Modular Arithmetic & Random Witness Vectors");
        result.setOutput(res.isProbablePrime ? "PROBABLE PRIME (Passed " + kRounds + " rounds)" : "COMPOSITE");
        result.setExecutionTimeNs(duration);
        result.setComparisons(kRounds);
        result.setIterations(kRounds);
        result.setSwaps(0);
        result.setRecursionCount(0);
        result.setTimeComplexity("O(k × log³ n)");
        result.setSpaceComplexity("O(log n) Bit Storage");
        result.setExplanation(String.format("Executed %d randomized witness rounds. Result: %s.",
                kRounds, res.isProbablePrime ? "Probable Prime" : "Composite"));
        result.setSteps(steps);

        return result;
    }
}
