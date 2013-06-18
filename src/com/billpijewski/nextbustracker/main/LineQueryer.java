package com.billpijewski.nextbustracker.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * @author Bill Pijewski
 */
public class LineQueryer extends Thread {

	private String line;

	/**
	 * @param line
	 */
	public LineQueryer(String line) {
		super();
		this.line = line;
	}

	@Override
	public void run() {
		while (true) {
			long start = System.currentTimeMillis();
			Long lastTime = new Long(0);

			try {
				Document document = queryBusLocations(line, lastTime);
				Element root = document.getRootElement();
				Element timeElem = root.getChild("lastTime");

				if (timeElem == null) 
					continue; 
				
				lastTime = Long.parseLong(timeElem.getAttributeValue("time"));

				Calendar cal = new GregorianCalendar();
				cal.setTimeZone(TimeZone.getTimeZone("US/Pacific"));
				cal.setTimeInMillis(lastTime);

				File dir = new File(NextBusTracker.OUTPUT_DIR + "/"
						+ NextBusTracker.AGENCY + "/"
						+ line + "/"
						+ cal.get(Calendar.YEAR) + "/"
						+ (cal.get(Calendar.MONTH) + 1) + "/"
						+ cal.get(Calendar.DAY_OF_MONTH) + "/");
				dir.mkdirs();
				
				File file = new File(dir, line + "_" + lastTime + ".xml");
				FileOutputStream ostream = new FileOutputStream(file);

				writeXMLData(document, ostream);

				System.out.println("Wrote " + file.getCanonicalPath());

				long end = System.currentTimeMillis();
				long wait = Math.max(20000 - (end - start), 0);

				Thread.sleep(wait); // 20 seconds
			} catch (Exception e) {
				System.err.println(e.getLocalizedMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param line
	 * @param time
	 * @return
	 * @throws URISyntaxException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JDOMException
	 */
	private Document queryBusLocations(String line, Long time)
			throws URISyntaxException, ClientProtocolException, IOException,
			JDOMException {
		DefaultHttpClient httpclient = new DefaultHttpClient();

		try {		
            httpclient.addRequestInterceptor(new HttpRequestInterceptor() {
                public void process(
                        final HttpRequest request,
                        final HttpContext context) throws HttpException, IOException {
                    if (!request.containsHeader("Accept-Encoding")) {
                        request.addHeader("Accept-Encoding", "gzip");
                    }
                }
            });

            httpclient.addResponseInterceptor(new HttpResponseInterceptor() {
                public void process(
                        final HttpResponse response,
                        final HttpContext context) throws HttpException, IOException {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        Header ceheader = entity.getContentEncoding();
                        if (ceheader != null) {
                            HeaderElement[] codecs = ceheader.getElements();
                            for (int i = 0; i < codecs.length; i++) {
                                if (codecs[i].getName().equalsIgnoreCase("gzip")) {
                                    response.setEntity(
                                            new GzipDecompressingEntity(response.getEntity()));
                                    return;
                                }
                            }
                        }
                    }
                }

            });
			
			URIBuilder ubuilder = new URIBuilder();
			ubuilder.setScheme("http")
					.setHost(NextBusTracker.HOST)
					.setPath(NextBusTracker.FEED_PATH)
					.setParameter("command", "vehicleLocations")
					.setParameter("a", NextBusTracker.AGENCY)
					.setParameter("r", line)
					.setParameter("t", time.toString());
			URI uri = ubuilder.build();
			HttpGet httpget = new HttpGet(uri);

			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String responseBody = httpclient.execute(httpget, responseHandler);

			SAXBuilder builder = new SAXBuilder();
			Document document = builder.build(new StringReader(responseBody));

			return (document);
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
	}

	private void writeXMLData(Document document, OutputStream ostream)
			throws IOException {
		XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
		xout.output(document, ostream);
	}
}