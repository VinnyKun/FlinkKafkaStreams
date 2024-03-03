import model.KafkaEvent;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import serdes.KafkaEventDeserializer;
import serdes.KafkaEventSerializer;

import java.util.regex.Pattern;

public class Main {
    public static final String BOOTSTRAP_SERVERS = "kafka0:9094,kafka1:9094,kafka2:9092";
    public static final String SOURCE_TOPIC = "messages-raw";
    public static final String SINK_TOPIC = "messages-censored";

    public static void main(String[] args) throws Exception {
        KafkaSource<KafkaEvent> source = KafkaSource.<KafkaEvent>builder()
                .setBootstrapServers(BOOTSTRAP_SERVERS)
                .setTopics(SOURCE_TOPIC)
                .setGroupId("flink-messages-censor")
                .setStartingOffsets(OffsetsInitializer.committedOffsets(OffsetResetStrategy.EARLIEST))
                .setDeserializer(new KafkaEventDeserializer(SOURCE_TOPIC))
                .setProperty("partition.discovery.interval.ms", "60000")
                .build();

        KafkaSink<KafkaEvent> sink = KafkaSink.<KafkaEvent>builder()
                .setBootstrapServers(BOOTSTRAP_SERVERS)
                .setRecordSerializer(new KafkaEventSerializer(SINK_TOPIC))
                .setTransactionalIdPrefix("flink")
                .build();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment().enableCheckpointing(60000);
        env.fromSource(source, WatermarkStrategy.noWatermarks(), SOURCE_TOPIC)
                .map(x -> {
                    String censoredBody = censorText(x.value.getBody());
                    x.value.setBody(censoredBody);
                    return new KafkaEvent(x.key, x.value, x.timestamp);
                })
                .sinkTo(sink).name(SINK_TOPIC);

        env.execute("Message Censor");
    }

    private static String censorText(String text) {
        Pattern patternFuck = Pattern.compile("fuck", Pattern.CASE_INSENSITIVE);
        Pattern patternShit = Pattern.compile("shit", Pattern.CASE_INSENSITIVE);
        Pattern patternBitch = Pattern.compile("bitch", Pattern.CASE_INSENSITIVE);
        text = patternFuck.matcher(text).replaceAll("f**k");
        text = patternShit.matcher(text).replaceAll("s**t");
        text = patternBitch.matcher(text).replaceAll("b***h");
        return text;
    }
}
