package io.friday.registry.core.raft;

import io.friday.registry.core.RegistryServerDemo;
import io.friday.registry.core.entity.Address;
import io.friday.registry.core.raft.Impl.NodeImpl;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RaftDemo {
    public static void main(String[] args) throws Exception {
        try {
            int port = Integer.parseInt(System.getenv("port"));
            NodeImpl raftNode = new NodeImpl("localhost", port);
            raftNode.start();
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            while (true){
                String line = input.readLine();
                System.out.println("命令: " + line);
                if (StringUtils.isNumeric(line)) {
                    int destPort = Integer.parseInt(line);
                    raftNode.connectPeer(new Address("localhost", destPort));
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
