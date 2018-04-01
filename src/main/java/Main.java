import com.google.gson.Gson;
import com.google.gson.JsonElement;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.ProtoClient;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Node;
import io.kubernetes.client.models.V1NodeList;
import io.kubernetes.client.models.V1NodeSpec;
import io.kubernetes.client.models.V1ObjectMeta;
import io.kubernetes.client.proto.V1;
import io.kubernetes.client.util.Config;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException, ApiException {

        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);

        CoreV1Api api = new CoreV1Api();

        V1NodeList v1NodeList = api.listNode(null, null, null, null, null, null, null, null, null);

        List<V1Node> nodes = v1NodeList.getItems();

        for (int i = 0; i < nodes.size(); i++) {
            String label = "";
            switch (i) {
                case 0:
                case 1:
                case 2:
                    label = "broker";
                    break;
                case 3:
                case 4:
                case 5:
                    label = "zookeeper";
                    break;
            }
            V1Node node = nodes.get(i);


            Map<String, String> labels = node.getMetadata().getLabels();
            labels.put("cluster", label);
            node.getMetadata().setLabels(labels);

            String name = node.getMetadata().getName();
            api.replaceNode(name, node, "false");


        }


        v1NodeList.getItems()
                .forEach(v1Node -> {
                    System.out.println(v1Node.getMetadata().getName());
                    v1Node.getMetadata()
                            .getLabels().entrySet().stream()
                            .filter(entry -> entry.getKey().equals("cluster"))
                            .forEach(entry -> System.out.println("\t" + entry.getKey() + ": " + entry.getValue()));
                    System.out.println();

                });


    }


}
