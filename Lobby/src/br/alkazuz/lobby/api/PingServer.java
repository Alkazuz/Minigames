package br.alkazuz.lobby.api;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class PingServer
{
    private InetSocketAddress host;
    public JSONObject json;
    public int maxPlayers;
    public String motd;
    public int onlinePlayers;
    private int timeoutInt;
    
    public PingServer(String ip, int port) {
        this.maxPlayers = 0;
        this.motd = "";
        this.onlinePlayers = 0;
        this.timeoutInt = 100;
        this.host = new InetSocketAddress(ip, port);
        try {
            this.fetchData();
        }
        catch (Exception ex) {}
    }
    
    private void fetchData() throws Exception {
        Socket socket = new Socket();
        socket.setSoTimeout(this.timeoutInt);
        socket.connect(this.host, this.timeoutInt);
        OutputStream outputStream = socket.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        InputStream inputStream = socket.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream handshake = new DataOutputStream(b);
        handshake.writeByte(0);
        this.writeVarInt(handshake, 47);
        this.writeVarInt(handshake, this.host.getHostString().length());
        handshake.writeBytes(this.host.getHostString());
        handshake.writeShort(this.host.getPort());
        this.writeVarInt(handshake, 1);
        this.writeVarInt(dataOutputStream, b.size());
        dataOutputStream.write(b.toByteArray());
        dataOutputStream.writeByte(1);
        dataOutputStream.writeByte(0);
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        this.readVarInt(dataInputStream);
        int id = this.readVarInt(dataInputStream);
        if (id == -1) {
            dataOutputStream.close();
            outputStream.close();
            inputStreamReader.close();
            inputStream.close();
            socket.close();
            throw new IOException("Ocorreu um erro ao verificar o dataInputStream");
        }
        int length = this.readVarInt(dataInputStream);
        if (length == -1) {
            dataOutputStream.close();
            outputStream.close();
            inputStreamReader.close();
            inputStream.close();
            socket.close();
            throw new IOException("Ocorreu um erro ao verificar o dataInputStream");
        }
        byte[] in = new byte[length];
        dataInputStream.readFully(in);
        String json = new String(in);
        long now = System.currentTimeMillis();
        dataOutputStream.writeByte(9);
        dataOutputStream.writeByte(1);
        dataOutputStream.writeLong(now);
        this.readVarInt(dataInputStream);
        id = this.readVarInt(dataInputStream);
        if (id == -1) {
            dataOutputStream.close();
            outputStream.close();
            inputStreamReader.close();
            inputStream.close();
            socket.close();
            throw new IOException("ERRO");
        }
        this.json = (JSONObject)new JSONParser().parse(json);
        dataOutputStream.close();
        outputStream.close();
        inputStreamReader.close();
        inputStream.close();
        socket.close();
        this.processAll();
    }
    
    private JSONObject getJson() {
        if (this.json == null) {
            return null;
        }
        return this.json;
    }
    
    public int getMaxPlayers() {
        return this.maxPlayers;
    }
    
    public String getMotd() {
        if (this.motd == "" || this.motd.equalsIgnoreCase("")) {
            return "§4§lOffline";
        }
        return this.motd;
    }
    
    public int getOnlinePlayers() {
        return this.onlinePlayers;
    }
    
    public boolean isOnline() {
        return this.json != null;
    }
    
    public void processAll() {
        this.motd = String.valueOf(this.getJson().get((Object)"description")).replace("\u00c2", "");
        JSONArray array = new JSONArray();
        JSONObject site = null;
        array.add(this.getJson().get((Object)"players"));
        for (Object anArray : array) {
            site = (JSONObject)anArray;
        }
        this.maxPlayers = (int)(long)site.get((Object)"max");
        this.onlinePlayers = (int)(long)site.get((Object)"online");
    }
    
    private int readVarInt(DataInputStream in) throws IOException {
        int i = 0;
        int j = 0;
        byte k;
        do {
            k = in.readByte();
            i |= (k & 0x7F) << j++ * 7;
            if (j <= 5) {
                continue;
            }
            throw new RuntimeException("VarInt muito grande");
        } while ((k & 0x80) == 0x80);
        return i;
    }
    
    private void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
        while ((paramInt & 0xFFFFFF80) != 0x0) {
            out.writeByte((paramInt & 0x7F) | 0x80);
            paramInt >>>= 7;
        }
        out.writeByte(paramInt);
    }
}
