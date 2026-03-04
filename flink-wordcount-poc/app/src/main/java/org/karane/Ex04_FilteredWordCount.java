package org.karane;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Ex04_FilteredWordCount {

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "the", "a", "an", "is", "it", "in", "to", "of", "and", "for",
            "on", "with", "at", "by", "or", "as", "that", "this", "from",
            "be", "not", "no", "we", "us", "so", "has", "its", "tis"
    ));

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<String> lines = env.readTextFile("/data/shakespeare.txt");

        // Tokenize --> remove stop words --> remove short words --> count
        DataStream<Tuple2<String, Integer>> counts = lines
                .flatMap(new Tokenizer())
                .filter(new StopWordFilter())
                .filter(new MinLengthFilter(3))
                .keyBy(value -> value.f0)
                .sum(1);

        System.out.println("=== Ex04 — Filtered Word Count (stop words removed, min length 3) ===");
        counts.print();

        env.execute("Ex04 — Filtered Word Count");
    }

    public static class Tokenizer implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String line, Collector<Tuple2<String, Integer>> out) {
            for (String word : line.toLowerCase().split("\\W+")) {
                if (!word.isEmpty()) {
                    out.collect(Tuple2.of(word, 1));
                }
            }
        }
    }

    public static class StopWordFilter implements FilterFunction<Tuple2<String, Integer>> {
        @Override
        public boolean filter(Tuple2<String, Integer> value) {
            return !STOP_WORDS.contains(value.f0);
        }
    }

    public static class MinLengthFilter implements FilterFunction<Tuple2<String, Integer>> {
        private final int minLength;

        public MinLengthFilter(int minLength) {
            this.minLength = minLength;
        }

        @Override
        public boolean filter(Tuple2<String, Integer> value) {
            return value.f0.length() >= minLength;
        }
    }
}
