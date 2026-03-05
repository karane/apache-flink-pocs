package org.karane;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class Ex05_WordLengthAnalytics {

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<String> lines = env.readTextFile("/data/lorem.txt");

        // Count words per length bucket
        DataStream<Tuple2<String, Integer>> lengthCounts = lines
                .flatMap(new WordExtractor())
                .map(new LengthBucketMapper())
                .keyBy(value -> value.f0)
                .sum(1);

        System.out.println("=== Ex05 — Word Length Analytics ===");
        System.out.println("--- Words per length bucket ---");
        lengthCounts.print();

        env.execute("Ex05 — Word Length Analytics");
    }

    public static class WordExtractor implements FlatMapFunction<String, String> {
        @Override
        public void flatMap(String line, Collector<String> out) {
            for (String word : line.toLowerCase().split("\\W+")) {
                if (!word.isEmpty()) {
                    out.collect(word);
                }
            }
        }
    }

    public static class LengthBucketMapper implements MapFunction<String, Tuple2<String, Integer>> {
        @Override
        public Tuple2<String, Integer> map(String word) {
            int len = word.length();
            String bucket;
            if (len <= 3) {
                bucket = "short (1-3)";
            } else if (len <= 6) {
                bucket = "medium (4-6)";
            } else if (len <= 9) {
                bucket = "long (7-9)";
            } else {
                bucket = "very long (10+)";
            }
            return Tuple2.of(bucket, 1);
        }
    }
}
