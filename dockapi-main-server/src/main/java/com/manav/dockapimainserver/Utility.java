package com.manav.dockapimainserver;

import java.util.Random;

public class Utility {

    public static String generateSlug() {
        String[] adjectives = {
                "quick", "lazy", "sleepy", "happy", "sad", "brave", "calm", "bright", "cool", "kind",
                "clever", "funny", "gentle", "lucky", "wild", "fierce", "shy", "strong", "quiet", "bold",
                "smart", "zany", "witty", "proud", "curious", "friendly", "grumpy", "jolly", "loyal", "noble",
                "playful", "thoughtful", "wise", "eager", "fearless", "cheerful", "humble", "energetic", "honest", "charming",
                "graceful", "patient", "polite", "silly", "spunky", "trusty", "vivid", "wary", "zealous", "mellow",
                "adventurous", "artistic", "athletic", "courageous", "determined", "dynamic", "enthusiastic", "generous", "imaginative", "inventive",
                "logical", "meticulous", "optimistic", "passionate", "perceptive", "practical", "rational", "reliable", "resilient", "resourceful",
                "sensitive", "sincere", "spirited", "tenacious", "tolerant", "versatile", "vibrant", "whimsical", "zealous", "adaptable",
                "affectionate", "ambitious", "bold", "charismatic", "dedicated", "disciplined", "empathetic", "forgiving", "hardworking", "intelligent",
                "inventive", "motivated", "outgoing", "persevering", "respectful", "selfless", "skilled", "supportive", "thoughtful", "unique"
        };

        String[] nouns = {
                "fox", "dog", "cat", "mouse", "bear", "lion", "tiger", "wolf", "bird", "fish",
                "rabbit", "owl", "eagle", "shark", "whale", "dolphin", "seal", "penguin", "koala", "kangaroo",
                "elephant", "giraffe", "zebra", "cheetah", "leopard", "panther", "hippo", "rhino", "crocodile", "alligator",
                "antelope", "buffalo", "camel", "deer", "ferret", "goat", "hamster", "hedgehog", "iguana", "jaguar",
                "lemur", "meerkat", "mongoose", "otter", "peacock", "porcupine", "raccoon", "reindeer", "salamander", "squirrel",
                "tortoise", "vulture", "walrus", "yak", "armadillo", "badger", "bat", "beaver", "bobcat", "chameleon",
                "chipmunk", "cobra", "crane", "crow", "cuckoo", "donkey", "falcon", "gecko", "hawk", "heron",
                "kingfisher", "lizard", "lynx", "mole", "moose", "narwhal", "newt", "octopus", "ostrich", "parrot",
                "platypus", "quail", "robin", "seahorse", "skunk", "sloth", "sparrow", "starfish", "stork", "swan",
                "tapir", "toad", "turkey", "weasel", "wombat", "woodpecker", "yak", "zebra", "ant", "beetle"
        };


        Random random = new Random();
        String adjective = adjectives[random.nextInt(adjectives.length)];
        String noun = nouns[random.nextInt(nouns.length)];
        int number1 = random.nextInt(1000);
        int number2 = random.nextInt(100);
        String number = number1 + "-" + number2;
        return adjective+number2 + "-" + noun + "-" + number;
    }

}
