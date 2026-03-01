package org.karane;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class Ex01_BasicWordCount {

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // Word count from an in-memory collection
        DataStream<String> lines = env.fromElements(
                "hello world",
                "hello flink",
                "flink is great",
                "hello world flink"
        );

        DataStream<Tuple2<String, Integer>> counts = lines
                .flatMap(new Tokenizer())
                .keyBy(value -> value.f0)
                .sum(1);

        System.out.println("=== Ex01 — Basic Word Count ===");
        counts.print();

        env.execute("Ex01 — Basic Word Count");
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
}
