

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import java.nio.ByteBuffer

class VideoEncoder(
    private val width: Int,
    private val height: Int,
    private val fps: Int,
    private val outputPath: String
) {
    private var mediaCodec: MediaCodec? = null
    private var mediaMuxer: MediaMuxer? = null
    private var trackIndex: Int = -1
    private var frameIndex: Long = 0
    private var bufferInfo: MediaCodec.BufferInfo = MediaCodec.BufferInfo()
    private val frameDuration: Long = 1_000_000L / fps // 마이크로초 단위

    fun start() {
        val format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height)
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible)
        format.setInteger(MediaFormat.KEY_BIT_RATE, 2000000)
        format.setInteger(MediaFormat.KEY_FRAME_RATE, fps)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)

        mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
        mediaCodec?.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        mediaCodec?.start()

        mediaMuxer = MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
    }

    fun encodeFrame(input: ByteBuffer) {
        val inputBufferIndex = mediaCodec?.dequeueInputBuffer(10000)
        if (inputBufferIndex != null && inputBufferIndex >= 0) {
            val inputBuffer = mediaCodec?.getInputBuffer(inputBufferIndex)
            inputBuffer?.clear()
            inputBuffer?.put(input)
            mediaCodec?.queueInputBuffer(inputBufferIndex, 0, input.capacity(), frameIndex * frameDuration, 0)
            frameIndex++
        }

        var outputBufferIndex = mediaCodec?.dequeueOutputBuffer(bufferInfo, 10000)
        while (outputBufferIndex != null && outputBufferIndex >= 0) {
            val outputBuffer = mediaCodec?.getOutputBuffer(outputBufferIndex)
            if (trackIndex == -1) {
                val newFormat = mediaCodec?.getOutputFormat(outputBufferIndex)
                trackIndex = mediaMuxer?.addTrack(newFormat!!) ?: -1
                mediaMuxer?.start()
            }

            outputBuffer?.position(bufferInfo.offset)
            outputBuffer?.limit(bufferInfo.offset + bufferInfo.size)

            mediaMuxer?.writeSampleData(trackIndex, outputBuffer!!, bufferInfo)
            mediaCodec?.releaseOutputBuffer(outputBufferIndex, false)

            outputBufferIndex = mediaCodec?.dequeueOutputBuffer(bufferInfo, 0)
        }
    }

    fun finish() {
        val inputBufferIndex = mediaCodec?.dequeueInputBuffer(10000)
        if (inputBufferIndex != null && inputBufferIndex >= 0) {
            mediaCodec?.queueInputBuffer(inputBufferIndex, 0, 0, frameIndex * frameDuration, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
        }

        var outputBufferIndex = mediaCodec?.dequeueOutputBuffer(bufferInfo, 10000)
        while (outputBufferIndex != null && outputBufferIndex >= 0) {
            val outputBuffer = mediaCodec?.getOutputBuffer(outputBufferIndex)
            if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                break
            }
            if (bufferInfo.size != 0) {
                outputBuffer?.position(bufferInfo.offset)
                outputBuffer?.limit(bufferInfo.offset + bufferInfo.size)
                mediaMuxer?.writeSampleData(trackIndex, outputBuffer!!, bufferInfo)
            }
            mediaCodec?.releaseOutputBuffer(outputBufferIndex, false)
            outputBufferIndex = mediaCodec?.dequeueOutputBuffer(bufferInfo, 10000)
        }

        mediaCodec?.stop()
        mediaCodec?.release()
        mediaMuxer?.stop()
        mediaMuxer?.release()
    }
}