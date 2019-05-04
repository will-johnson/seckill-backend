package com.seen.seckillbackend.util;

import com.seen.seckillbackend.rabbitmq.MQReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Logg {
  public static Logger logger = LoggerFactory.getLogger(MQReceiver.class);
}
