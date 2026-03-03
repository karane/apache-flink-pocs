package org.karane;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class Ex03_MultiFileWordCount {

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        String dataDir = args.length > 0 ? args[0] : "/data";

        // Read from multiple files and union
        DataStream<String> lorem = env.readTextFile(dataDir + "/lorem.txt");
        DataStream<String> shakespeare = env.readTextFile(dataDir + "/shakespeare.txt");
        DataStream<String> allLines = lorem.union(shakespeare);

        DataStream<Tuple2<String, Integer>> counts = allLines
                .flatMap(new Tokenizer())
                .keyBy(value -> value.f0)
                .sum(1);

        System.out.println("=== Ex03 — Multi-File Word Count ===");
        counts.print();

        env.execute("Ex03 — Multi-File Word Count");
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
