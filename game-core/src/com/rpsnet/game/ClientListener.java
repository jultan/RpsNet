package com.rpsnet.game;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.rpsnet.network.Packets;

public class ClientListener extends Listener
{
    GameClient gameClient;

    public ClientListener(GameClient g)
    {
        super();

        gameClient = g;
    }

    public void connected(Connection connection)
    {
        Packets.RegisterName registerName = new Packets.RegisterName();
        registerName.name = gameClient.getPlayerName();
        connection.sendTCP(registerName);

        connection.sendTCP(new Packets.PlayerCountRequest());

        gameClient.setServerConnection(connection);
    }

    public void disconnected(Connection connection)
    {
        gameClient.updateCurrentScreen();
    }

    public void received(Connection connection, Object o)
    {
        if(o instanceof Packets.PlayerCount)
        {
            gameClient.setPlayerCountInfo((Packets.PlayerCount)o);
        }
        else if(o instanceof Packets.RegisterNameResponse)
        {
            //If the server didn't accept the name registration, then abort the connection
            Packets.RegisterNameResponse response = (Packets.RegisterNameResponse)o;
            System.out.println(response.requestedName);
            System.out.print(response.responseType);

            if(response.responseType != Packets.RegisterNameResponse.ResponseType.ACCEPTED)
            {
                gameClient.abortConnection();
            }
        }
    }
}
