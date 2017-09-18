package b.x;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class TestXA {

	private static EmbeddedDatabase db;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private ServiceBeanA beanTestA;

	@Autowired
	private JmsTemplate jmsNoXATemplate;

	@Before
	public void setup() {

	}

	@BeforeClass
	public static void beforeClass() {
		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		db = builder.setType(EmbeddedDatabaseType.HSQL).addScript("init-schema.sql").build();
	}

	@AfterClass
	public static void afterClass() {
		db.shutdown();
	}

	@Test
	@Transactional
	@Rollback(value = false)
	public void testSuccess() {
		beanTestA.writeARecord(false);
		beanTestA.writeMsg(false);
	}

	@Test
	@Transactional
	@Rollback(value = false)
	public void testDBFail() {
		try {
			//runtime exception causes 2PC TX roll back no matter get caught or not
			beanTestA.writeARecord(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {
			beanTestA.writeMsg(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Transactional
	@Rollback(value = false)
	public void testJMSFail() {
		try {
			beanTestA.writeARecord(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {
			//runtime exception causes 2PC TX roll back no matter get caught or not
			beanTestA.writeMsg(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterTransaction
	public void afterTest() throws JMSException {
		System.out
				.println("After trx counting = " + JdbcTestUtils.countRowsInTable(jdbcTemplate, "SAMPLE_SCHEMA.TESTA"));
		TextMessage msg = (TextMessage) (jmsNoXATemplate.receive("test.qa"));
		if (msg != null) {
			System.out.println("After trx msg = " + msg.getText());
		}
	}

}
