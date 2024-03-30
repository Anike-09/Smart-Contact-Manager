package com.rank;


public class Main {
    public static void main(String[] args) {
        int[] ranks = {20, 5, 28, 67, 44}; // Array of ranks in ascending order
        int harperRank = 50; // Harper's rank to find

        int harperIndex = findHarperRank(ranks, harperRank);
        if (harperIndex != -1) {
            System.out.println("Harper's rank: " + (harperIndex + 1));
        } else {
            System.out.println("Harper's rank not found.");
        }
    }

    private static int findHarperRank(int[] ranks, int harperRank) {
        int low = 0;
        int high = ranks.length - 1;

        while (low <= high) {
            int mid = (low + high) / 2;
            if (ranks[mid] == harperRank) {
                return mid;
            } else if (ranks[mid] < harperRank) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return -1;
    }
}
