package io.friday.registry.raft;

import io.friday.registry.common.entity.Address;
import io.friday.registry.raft.Impl.DefaultRaftNode;
import io.friday.registry.transport.Impl.DefaultTransportNode;
import io.friday.registry.transport.TransportNode;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RaftDemo {
    public static void main(String[] args) throws Exception {
        try {
            int port = Integer.parseInt(System.getenv("port"));
            TransportNode transportNode = new DefaultTransportNode("localhost", port);
            DefaultRaftNode raftNode = new DefaultRaftNode("localhost", port, transportNode);

            transportNode.init();
            raftNode.init();
            transportNode.start();
            raftNode.start();

            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            while (true){
                String line = input.readLine();
                System.out.println("命令: " + line);
                if (StringUtils.isNumeric(line)) {
                    int destPort = Integer.parseInt(line);
                    raftNode.connectPeer(new Address("localhost", destPort));
//                    transportNode.connect(new Address("localhost", destPort));
                } else if (line.equals("list")) {
                    raftNode.listPeer();
                }
            }
//            SpringApplication.run(RegistryApplication.class, args);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
