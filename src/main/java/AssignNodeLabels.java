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
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AssignNodeLabels {

    public static void main(String[] args) throws IOException, ApiException {

        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);

        CoreV1Api api = new CoreV1Api();

        V1NodeList v1NodeList = api.listNode(null, null, null, null, null, null, null, null, null);

        //*
        List<V1Node> nodes = v1NodeList.getItems();

        for (int i = 0; i < nodes.size(); i++) {
            String label = "";
            String[] nodeName = nodes.get(i).getMetadata().getName().split("-");
            switch (nodeName[nodeName.length-1]) {
                case "0":
                case "1":
                case "2":
                case "11":
                case "13":
                case "15":
                    label = "broker";
                    break;
                case "3":
                case "4":
                case "5":
                    label = "zookeeper";
                    break;
                case "6":
                case "7":
                case "8":
                case "9":
                case "10":
                case "12":
                case "14":
                case "16":
                case "17":
                    label = "producer";
                    break;
                default:
                    label = "";
                    break;
            }

            if (!label.isEmpty()) {

                V1Node node = nodes.get(i);

                Map<String, String> labels = node.getMetadata().getLabels();
                labels.put("cluster", label);
                node.getMetadata().setLabels(labels);

                String name = node.getMetadata().getName();
                api.replaceNode(name, node, "false");
            }
        }
        //*/

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
