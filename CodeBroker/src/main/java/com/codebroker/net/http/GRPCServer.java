//package com.codebroker.net.http;
//
//import akka.actor.typed.ActorSystem;
//import akka.actor.typed.javadsl.Adapter;
//import akka.http.javadsl.*;
//import akka.http.javadsl.model.HttpRequest;
//import akka.http.javadsl.model.HttpResponse;
//import akka.japi.Function;
//import akka.stream.SystemMaterializer;
//import com.codebroker.grpc.GreeterService;
//import com.codebroker.grpc.GreeterServiceHandlerFactory;
//
//import javax.net.ssl.KeyManagerFactory;
//import javax.net.ssl.SSLContext;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.security.KeyFactory;
//import java.security.KeyStore;
//import java.security.PrivateKey;
//import java.security.SecureRandom;
//import java.security.cert.Certificate;
//import java.security.cert.CertificateFactory;
//import java.security.spec.PKCS8EncodedKeySpec;
//import java.util.Base64;
//import java.util.concurrent.CompletionStage;
//
//public class GRPCServer {
//	final ActorSystem<?> system;
//	final int port;
//
//	public GRPCServer(ActorSystem<?> system,int port) {
//		this.system = system;
//		this.port=port;
//	}
//
//	public CompletionStage<ServerBinding> run() throws Exception {
//
//		Function<HttpRequest, CompletionStage<HttpResponse>> service =
//				GreeterServiceHandlerFactory.create(
//						new GreeterServiceImpl(system),
//						system);
//
//		CompletionStage<ServerBinding> bind = Http.get(system).newServerAt("127.0.0.1", port)
//				.bind((akka.japi.function.Function<HttpRequest, CompletionStage<HttpResponse>>) service);
//
////		CompletionStage<ServerBinding> bound =
////				// Akka HTTP 10.1 requires adapters to accept the new actors APIs
////				Http.get(Adapter.toClassic(system)).bindAndHandleAsync(
////						service,
////						ConnectWithHttps.toHostHttps("127.0.0.1", port)
////								.withCustomHttpsContext(serverHttpContext()),
////						SystemMaterializer.get(system).materializer()
////				);
//
//		bind.thenAccept(binding ->
//				System.out.println("gRPC server bound to: " + binding.localAddress())
//		);
//
//		return bind;
//	}
//	// #server
//
//
//	// FIXME this will be replaced by a more convenient utility, see https://github.com/akka/akka-grpc/issues/89
//	private static HttpsConnectionContext serverHttpContext() throws Exception {
//		String keyEncoded = read(GreeterService.class.getResourceAsStream("/certs/server1.key"))
//				.replace("-----BEGIN PRIVATE KEY-----\n", "")
//				.replace("-----END PRIVATE KEY-----\n", "")
//				.replace("\n", "");
//
//		byte[] decodedKey = Base64.getDecoder().decode(keyEncoded);
//
//		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decodedKey);
//
//		KeyFactory kf = KeyFactory.getInstance("RSA");
//		PrivateKey privateKey = kf.generatePrivate(spec);
//
//		CertificateFactory fact = CertificateFactory.getInstance("X.509");
//		Certificate cer =
//				fact.generateCertificate(GreeterService.class.getResourceAsStream("/certs/server1.pem"));
//
//		KeyStore ks = KeyStore.getInstance("PKCS12");
//		ks.load(null);
//		ks.setKeyEntry("private", privateKey, new char[0], new Certificate[]{ cer });
//
//		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
//		keyManagerFactory.init(ks, null);
//
//		SSLContext context = SSLContext.getInstance("TLS");
//		context.init(keyManagerFactory.getKeyManagers(), null, new SecureRandom());
//
//		return ConnectionContext.https(context);
//	}
//
//	private static String read(InputStream in) throws IOException {
//		ByteArrayOutputStream baos = new ByteArrayOutputStream(Math.max(64, in.available()));
//		byte[] buffer = new byte[32 * 1024];
//
//		int bytesRead = in.read(buffer);
//		while (bytesRead >= 0) {
//			baos.write(buffer, 0, bytesRead);
//			bytesRead = in.read(buffer);
//		}
//
//		return new String(baos.toByteArray(), "UTF-8");
//	}
//}
