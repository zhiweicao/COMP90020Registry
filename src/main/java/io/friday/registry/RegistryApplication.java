package io.friday.registry;

import io.friday.registry.core.RegistryServerDemo;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;

public class RegistryApplication {

    public static void main(String[] args) throws Exception {
        try {
            int port = Integer.parseInt(System.getenv("port"));
            RegistryServerDemo registryServerDemo = new RegistryServerDemo("localhost", port);
            registryServerDemo.init();
            registryServerDemo.start();
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            while (true){
                String line = input.readLine();
                System.out.println("命令: " + line);
                if (StringUtils.isNumeric(line)) {
                    int destPort = Integer.parseInt(line);
                    registryServerDemo.connect("localhost", destPort);
                } else if (line.equals("list")) {
                    registryServerDemo.list();
                }
            }
//            SpringApplication.run(RegistryApplication.class, args);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
