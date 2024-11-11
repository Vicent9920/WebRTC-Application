package com.vincent.webrtc.application.rtc

import android.content.Context
import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.RtpReceiver
import org.webrtc.SessionDescription

class WebRTCClient(context: Context) {
    private lateinit var socket: Socket
    private lateinit var peerConnectionFactory: PeerConnectionFactory
    private lateinit var peerConnection: PeerConnection
    private var localStream: MediaStream? = null

    // WebRTC 配置
    private val rtcConfig = PeerConnection.RTCConfiguration(listOf(
        PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer()
    ))

    init {
        // 初始化 PeerConnectionFactory
        val initializationOptions = PeerConnectionFactory.InitializationOptions.builder(context).createInitializationOptions()
        PeerConnectionFactory.initialize(initializationOptions)
        peerConnectionFactory = PeerConnectionFactory.builder().createPeerConnectionFactory()

        // 初始化 Socket.io
        socket = IO.socket("http://120.55.39.151:3000")
        setupSocketListeners()
    }

    private fun setupSocketListeners() {
        socket.on(Socket.EVENT_CONNECT) {
            println("Socket connected")
        }

        socket.on("offer") { args ->
            val offer = args[0] as String
            handleOffer(offer)
        }

        socket.on("answer") { args ->
            val answer = args[0] as String
            handleAnswer(answer)
        }

        socket.on("ice-candidate") { args ->
            val candidate = args[0] as String
            handleIceCandidate(candidate)
        }

        socket.connect()
    }

    // 创建 PeerConnection 并添加本地流
    private fun createPeerConnection() {
        peerConnection = peerConnectionFactory.createPeerConnection(rtcConfig, object : PeerConnection.Observer {
            override fun onSignalingChange(newState: PeerConnection.SignalingState?) {
                Log.d("WebRTC", "Signaling state changed: $newState")
            }

            override fun onIceConnectionChange(newState: PeerConnection.IceConnectionState?) {
                Log.d("WebRTC", "ICE connection state changed: $newState")
            }

            override fun onIceConnectionReceivingChange(receiving: Boolean) {
                Log.d("WebRTC", "ICE connection receiving changed: $receiving")
            }

            override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {
            }

            override fun onIceCandidate(candidate: IceCandidate) {
                Log.d("WebRTC", "ICE Candidate generated: $candidate")
                socket.emit("ice-candidate", candidate)
            }

            override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {
            }

            override fun onAddStream(stream: MediaStream) {
                Log.d("WebRTC", "Remote stream added")
            }

            override fun onRemoveStream(p0: MediaStream?) {
            }

            override fun onDataChannel(p0: DataChannel?) {
            }

            override fun onRenegotiationNeeded() {
            }

            override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {
            }

            // 其他 WebRTC 回调方法
        })!!

        localStream = peerConnectionFactory.createLocalMediaStream("LOCAL_STREAM")
        // 配置摄像头和麦克风
    }

    // 处理接收到的 offer
    private fun handleOffer(offer: String) {
        peerConnection.setRemoteDescription(SdpObserverAdapter(), SessionDescription(SessionDescription.Type.OFFER, offer))
        peerConnection.createAnswer(object : SdpObserverAdapter() {
            override fun onCreateSuccess(sdp: SessionDescription?) {
                peerConnection.setLocalDescription(SdpObserverAdapter(), sdp)
                socket.emit("answer", sdp?.description)
            }
        }, MediaConstraints())
    }

    // 处理接收到的 answer
    private fun handleAnswer(answer: String) {
        peerConnection.setRemoteDescription(SdpObserverAdapter(), SessionDescription(SessionDescription.Type.ANSWER, answer))
    }

    // 处理接收到的 ICE candidate
    private fun handleIceCandidate(candidate: String) {
        val iceCandidate = IceCandidate(null, 0, candidate)
        peerConnection.addIceCandidate(iceCandidate)
    }

    // 创建并发送 offer
    fun initiateCall() {
        createPeerConnection()
        peerConnection.createOffer(object : SdpObserverAdapter() {
            override fun onCreateSuccess(sdp: SessionDescription?) {
                peerConnection.setLocalDescription(SdpObserverAdapter(), sdp)
                socket.emit("offer", sdp?.description)
            }
        }, MediaConstraints())
    }
}