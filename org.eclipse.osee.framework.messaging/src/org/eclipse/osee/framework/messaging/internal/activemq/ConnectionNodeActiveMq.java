/*
 * Created on Feb 16, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal.activemq;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TemporaryTopic;
import javax.jms.Topic;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.MessageID;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;
import org.eclipse.osee.framework.messaging.future.ConnectionNode;
import org.eclipse.osee.framework.messaging.future.NodeInfo;

/**
 * @author b1528444
 */
public class ConnectionNodeActiveMq implements ConnectionNode, MessageListener {

   private String version;
   private String sourceId;
   private NodeInfo nodeInfo;
   private ExecutorService executor;
   private Connection connection;
   private Session session;
   private TemporaryTopic temporaryTopic;
   private MessageConsumer replyToConsumer;
   private Map<String, OseeMessagingListener> replyListeners;
   private Map<String, ActiveMqMessageListenerWrapper> regularListeners;

   private MessageProducer replyProducer;

   public ConnectionNodeActiveMq(String version, String sourceId, NodeInfo nodeInfo, ExecutorService executor, Connection connection) throws JMSException {
      this.version = version;
      this.sourceId = sourceId;
      this.nodeInfo = nodeInfo;
      this.executor = executor;
      this.connection = connection;
      regularListeners = new ConcurrentHashMap<String, ActiveMqMessageListenerWrapper>();
      replyListeners = new ConcurrentHashMap<String, OseeMessagingListener>();
      session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
      temporaryTopic = session.createTemporaryTopic();
      replyToConsumer = session.createConsumer(temporaryTopic);
      replyToConsumer.setMessageListener(this);
      replyProducer = session.createProducer(null);
   }

   @Override
   public void send(MessageID topic, Object body, OseeMessagingStatusCallback statusCallback) throws OseeCoreException {
      try {
         if (topic.isTopic()) {
            Topic destination = session.createTopic(topic.getGuid());
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            Message msg = ActiveMqUtil.createMessage(session, topic.getSerializationClass(), body);
            if (topic.isReplyRequired()) {
               msg.setJMSReplyTo(temporaryTopic);
            }
            producer.send(msg);
         }
      } catch (JMSException ex) {
         throw new OseeWrappedException(ex);
      }
   }

   @Override
   public void sendWithCorrelationId(String topic, Object body, Class<?> clazz, Object correlationId, OseeMessagingStatusCallback statusCallback) throws OseeCoreException {
      try {
         Topic destination = session.createTopic(topic);
         MessageProducer producer = session.createProducer(destination);
         producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
         Message msg = ActiveMqUtil.createMessage(session, clazz, body);
         msg.setJMSCorrelationID(correlationId.toString());

         producer.send(msg);
      } catch (JMSException ex) {
         statusCallback.fail(ex);
         throw new OseeWrappedException(ex);
      }
   }

   @Override
   public void subscribe(MessageID messageId, OseeMessagingListener listener, OseeMessagingStatusCallback statusCallback) {
      Topic destination;
      try {
         ActiveMqMessageListenerWrapper wrapperListener = regularListeners.get(messageId.getGuid());
         if (wrapperListener == null) {
            wrapperListener = new ActiveMqMessageListenerWrapper(replyProducer, session);
            regularListeners.put(messageId.getGuid(), wrapperListener);
            destination = session.createTopic(messageId.getGuid());
            MessageConsumer consumer = session.createConsumer(destination);
            consumer.setMessageListener(wrapperListener);
         }
         wrapperListener.addListener(listener);
      } catch (JMSException ex) {
         statusCallback.fail(ex);
      }
      statusCallback.success();
   }

   @Override
   public boolean subscribeToReply(MessageID messageId, OseeMessagingListener listener) {
      replyListeners.put(messageId.getGuid(), listener);
      return true;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.future.ConnectionNode#unsubscribe(org.eclipse.osee.framework.messaging.MessageID, org.eclipse.osee.framework.messaging.OseeMessagingListener, org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback)
    */
   @Override
   public void unsubscribe(MessageID messageId, OseeMessagingListener listener, OseeMessagingStatusCallback statusCallback) {
   }

   @Override
   public boolean unsubscribteToReply(MessageID messageId, OseeMessagingListener listener) {
      replyListeners.remove(messageId.getGuid());
      return true;
   }

   @Override
   public void onMessage(Message jmsMessage) {
      try {
         String correlationId = jmsMessage.getJMSCorrelationID();
         if (correlationId != null) {
            OseeMessagingListener listener = replyListeners.get(correlationId);
            if (listener != null) {
               listener.process(ActiveMqUtil.translateMessage(jmsMessage, listener.getClazz()), new HashMap(), new ReplyConnectionActiveMqImpl());
            }
         }
      } catch (JMSException ex) {
         OseeLog.log(ConnectionNodeActiveMq.class, Level.SEVERE, ex);
      } catch (OseeCoreException ex) {
         OseeLog.log(ConnectionNodeActiveMq.class, Level.SEVERE, ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.future.ConnectionNode#stop()
    */
   @Override
   public void stop() {
   }

}
