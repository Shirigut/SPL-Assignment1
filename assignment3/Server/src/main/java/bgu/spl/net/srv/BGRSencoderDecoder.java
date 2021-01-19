package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class BGRSencoderDecoder implements MessageEncoderDecoder<String> {
    private int byteCounter = 0;
    private byte[] opcode = new byte[2];
    private short opcodeNum;
    private byte[] bytes = new byte[1 << 16];
    private ArrayList<String> message = new ArrayList<>();
    private int numOfSeperators = 0;


    @Override
    public String decodeNextByte(byte nextByte) {
        byteCounter++;
        if (byteCounter >= bytes.length)
            pushByte();
        if (byteCounter <= 2) {
            opcode[byteCounter - 1] = nextByte;
            if (byteCounter == 2) {
                opcodeNum = bytesToShort(opcode);
                popString();
                // messages with opcode
                if (opcodeNum == 4 | opcodeNum == 11) {
                    return sendMessage();
                }
            }
        } else { // if bytesCounter > 2 (after opcode)
            //messages with course number
            if ((opcodeNum >= 5 & opcodeNum <= 10) & opcodeNum != 8) {
                bytes[byteCounter - 3] = nextByte;
                if (byteCounter == 4) {
                    popString();
                    return sendMessage();
                }
            }
            // messages with username
            if (opcodeNum == 8) {
                if (nextByte == '\0') {
                    popString();
                    return sendMessage();
                }
                bytes[byteCounter - 3] = nextByte;
            }

            if (opcodeNum >= 1 & opcodeNum <= 3) {
                if (nextByte == '\0') {
                    numOfSeperators++;
                    if (numOfSeperators == 1)
                        popString();
                    else {
                        popString();
                        return sendMessage();
                    }
                } else {
                    bytes[byteCounter - 3] = nextByte;
                }
            }
        }
        return null;
    }

    @Override
    public byte[] encode(String message) {
        // initialize parameters
        short opcode = Short.parseShort(message.substring(0, 2));
        byte[] opcodeByte = shortToBytes(opcode);
        short messageOpcode;
        byte[] messageOp;
        byte[] optional;
        byte[] toSend;

        // check if messageOpcode is one char or 2 and convert it to bytes
        if (message.charAt(3) != ' ')
            messageOpcode = Short.parseShort(message.substring(2, 4));
        else
            messageOpcode = Short.parseShort(message.substring(2, 3));
        messageOp = shortToBytes(messageOpcode);


        if (opcode == 13) {
            toSend = new byte[2 + messageOp.length];
            // add opcode and messageOpcode as bytes to the bytes array
            for (int i = 0; i < 4; i++) {
                if (i < 2)
                    toSend[i] = opcodeByte[i];
                else
                    toSend[i] = messageOp[i - 2];
            }

        } else {
            optional = message.substring(message.indexOf(' ') + 1).getBytes();
            toSend = new byte[2 + messageOp.length + optional.length];
            // add opcode and messageOpcode as bytes to the bytes array
            for (int i = 0; i < 4; i++) {
                if (i < 2)
                    toSend[i] = opcodeByte[i];
                else
                    toSend[i] = messageOp[i - 2];
            }
            // add optional data to the bytes array
            for (int i = 2 + messageOp.length; i < toSend.length; i++)
                toSend[i] = optional[i - (2 + messageOp.length)];
        }
        return toSend;
    }

    private void pushByte() {
        if (byteCounter >= bytes.length) {
            bytes = Arrays.copyOf(bytes, byteCounter * 2);
        }
    }

    private void popString() {
        String data;
        if (byteCounter == 2)
            data = String.valueOf(opcodeNum);
        else {
            if ((opcodeNum >= 5 & opcodeNum <= 10) & opcodeNum != 8) {
                short courseNum = bytesToShort(bytes);
                data = String.valueOf(courseNum);
            }
            else
                data = new String(bytes, 0, byteCounter - 3, StandardCharsets.UTF_8);
            for (int i = 0; i<byteCounter; i++)
                bytes[i]='\0';
            byteCounter = 2;
        }
        message.add(data);
    }

    private void clearFields() {
        opcodeNum = 0;
        byteCounter = 0;
        numOfSeperators = 0;
    }

    private String sendMessage() {
        String result = "";
        for (int i = 0; i < message.size(); i++) {
            if (i == 0)
                result = message.get(0);
            else result = result + " " + message.get(i);
        }
        clearFields();
        message.clear();
        return result;
    }

    public short bytesToShort(byte[] byteArr) {
        short result = (short) ((byteArr[0] & 0xff) << 8);
        result += (short) (byteArr[1] & 0xff);
        return result;
    }

    public byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte) ((num >> 8) & 0xFF);
        bytesArr[1] = (byte) (num & 0xFF);
        return bytesArr;
    }

}