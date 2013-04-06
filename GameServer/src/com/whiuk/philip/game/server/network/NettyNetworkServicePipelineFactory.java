package com.whiuk.philip.game.server.network;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.protobuf.ProtobufDecoder;
import org.jboss.netty.handler.codec.protobuf.ProtobufEncoder;
import org.jboss.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import org.jboss.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import com.whiuk.philip.game.shared.Messages.ClientMessage;

/**
 * Netty pipeline factory to integrate protobuf coding.
 * @author Philip Whitehouse
 *
 */
public class NettyNetworkServicePipelineFactory implements
		ChannelPipelineFactory {

	private NettyNetworkServiceHandler handler;

	/**
	 * 
	 * @param handler
	 */
	public NettyNetworkServicePipelineFactory(NettyNetworkServiceHandler handler) {
		super();
		this.handler = handler;
	}

	@Override
	public final ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline p = Channels.pipeline();
		p.addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
		p.addLast("protobufDecoder",
				new ProtobufDecoder(ClientMessage.getDefaultInstance()));
		p.addLast("frameEncoder",
				new ProtobufVarint32LengthFieldPrepender());
		p.addLast("protobufEncoder", new ProtobufEncoder());
		p.addLast("handler", handler);
		return p;
	}

}
