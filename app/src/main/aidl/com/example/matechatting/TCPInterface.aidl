// TCPInterface.aidl
package com.example.matechatting;
import com.example.matechatting.AcceptFriendListener;
// Declare any non-default types here with import statements

interface TCPInterface {
    void sendMsg(String message,int otherId);
    void acceptFriend(int friendId);
    void addFriend(int friendId,String uud);
    void registerAcceptCallback(AcceptFriendListener callback);
    void unRegisterAcceptCallback(AcceptFriendListener callBack);
}
