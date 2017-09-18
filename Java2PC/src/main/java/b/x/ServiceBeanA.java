package b.x;

import javax.jms.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ServiceBeanA {
	
	@Autowired
	private PlatformTransactionManager transactionManager;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private JmsTemplate jmsTemplate;

	@Autowired
	private Queue testDestination;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Transactional
	public void writeARecord(boolean fail) {
		jdbcTemplate.execute("insert into SAMPLE_SCHEMA.TESTA values ('1','Record 1')");
		if (fail)
			throw new RuntimeException("RT DB EX");
	}
	
	@Transactional
	public void writeMsg(boolean fail) {
		jmsTemplate.convertAndSend("test.qa", "hello world testA");
		if (fail)
			throw new RuntimeException("RT JMS EX");
	}
}
