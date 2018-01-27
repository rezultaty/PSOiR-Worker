package com.psoir.projekt;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

public class SqsListener {

	public static final String domainName = "Yudin";
	public static final String DATE = "Date";
	private static final String ITEM_NAME = "Item_Name";
	public static final String HTTPS_SQS_US_WEST_2_AMAZONAWS_COM = "https://sqs.us-east-1.amazonaws.com/799597092276/yudinsqs";
	public static final String ITEM = "Item";
	private final String queName;
	private final AmazonSQSClient sqs;
	private final ImgEditor imgEditor;


	public SqsListener() {
		AWSCredentials credentials = new BasicAWSCredentials("AKIAJ7WRA3SV7USSA3NA",
				"h5hsZiUXM+xqu4i6/DvKJBoojEVyuM/nZ+qcig03");
		this.queName = "yudinsqs";
		this.sqs = new AmazonSQSClient(credentials);
		this.sqs.setEndpoint(HTTPS_SQS_US_WEST_2_AMAZONAWS_COM);
		this.imgEditor = new ImgEditor(credentials);

	}

	public void listen() throws InterruptedException {
		while (true) {
			List<Message> messagesFromQueue = getMessagesFromQueue(getQueueUrl(this.queName));
			if (messagesFromQueue.size() > 0) {
				Message message = messagesFromQueue.get(0);
				List<ReplaceableAttribute> attributes = new ArrayList<>();
				attributes.add(new ReplaceableAttribute().withName(ITEM).withValue(message.getBody()));
				attributes.add(new ReplaceableAttribute().withName(DATE).withValue(DateTime.now().toString()));
				imgEditor.rotateImage(message.getBody());
				deleteMessageFromQueue(getQueueUrl(this.queName), message);

			} else {
				Thread.sleep(2000);
			}
		}
	}

	private List<Message> getMessagesFromQueue(String queueUrl) {
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
		List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
		return messages;

	}

	private String getQueueUrl(String queueName) {
		GetQueueUrlRequest getQueueUrlRequest = new GetQueueUrlRequest(queueName);
		return this.sqs.getQueueUrl(getQueueUrlRequest).getQueueUrl();
	}

	private void deleteMessageFromQueue(String queueUrl, Message message) {
		String messageRecieptHandle = message.getReceiptHandle();
		System.out.println("Original message : " + message);
		System.out.println("Original image : " + message.getBody());
		sqs.deleteMessage(new DeleteMessageRequest(queueUrl, messageRecieptHandle));

	}

}
