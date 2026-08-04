package com.app.tastybuds.data.repo

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

sealed interface SocketFrame {
    data object Open : SocketFrame
    data class Message(val text: String) : SocketFrame
    data object Closed : SocketFrame
    data object Failure : SocketFrame
}

@Singleton
class ChatWebSocketDataSource @Inject constructor(
    private val client: OkHttpClient
) {
    @Volatile
    private var socket: WebSocket? = null

    fun connect(url: String): Flow<SocketFrame> = callbackFlow {
        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                trySend(SocketFrame.Open)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                trySend(SocketFrame.Message(text))
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(NORMAL_CLOSURE, null)
                trySend(SocketFrame.Closed)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                trySend(SocketFrame.Failure)
            }
        }

        val ws = client.newWebSocket(Request.Builder().url(url).build(), listener)
        socket = ws

        awaitClose {
            ws.close(NORMAL_CLOSURE, null)
            socket = null
        }
    }

    fun send(text: String): Boolean = socket?.send(text) ?: false

    fun disconnect() {
        socket?.close(NORMAL_CLOSURE, null)
        socket = null
    }

    private companion object {
        const val NORMAL_CLOSURE = 1000
    }
}
