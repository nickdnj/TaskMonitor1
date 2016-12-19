/*
 * Copyright 2016 Mentor Graphics Corporation. All Rights Reserved.
 * <p>
 * Recipients who obtain this code directly from Mentor Graphics use it solely
 * for internal purposes to serve as example Java web services.
 * This code may not be used in a commercial distribution. Recipients may
 * duplicate the code provided that all notices are fully reproduced with
 * and remain in the code. No part of this code may be modified, reproduced,
 * translated, used, distributed, disclosed or provided to third parties
 * without the prior written consent of Mentor Graphics, except as expressly
 * authorized above.
 * <p>
 * THE CODE IS MADE AVAILABLE "AS IS" WITHOUT WARRANTY OR SUPPORT OF ANY KIND.
 * MENTOR GRAPHICS OFFERS NO EXPRESS OR IMPLIED WARRANTIES AND SPECIFICALLY
 * DISCLAIMS ANY WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * OR WARRANTY OF NON-INFRINGEMENT. IN NO EVENT SHALL MENTOR GRAPHICS OR ITS
 * LICENSORS BE LIABLE FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING LOST PROFITS OR SAVINGS) WHETHER BASED ON CONTRACT, TORT
 * OR ANY OTHER LEGAL THEORY, EVEN IF MENTOR GRAPHICS OR ITS LICENSORS HAVE BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * <p>
 */

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.dom.DOMSource;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.AttachmentPart;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.activation.DataHandler;
import javax.activation.DataSource;

import java.net.URL;
import java.util.Iterator;
import java.util.zip.GZIPOutputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.io.OutputStreamWriter;

/**
 * SOAP and XML manipulation utilities
 */
public abstract class WebServiceUtils
{
	private static MessageFactory _msgFact;
	private static SOAPConnection _connectionSOAP;
	private static Transformer _xmlTransformer;
	private static DocumentBuilder _domBuilder;

	private static final String SOAP_ATTACHMENT_FORMAT_XML = "application/xml";
	private static final String SOAP_ATTACHMENT_FORMAT_GZIP = "application/gzip";
    private static final String SOAP_ATTACHMENT_FORMAT_OCTECT = "application/octet-stream";

	private static MessageFactory getSOAPMsgFactory() throws SOAPException
	{
		if (_msgFact == null) {
			_msgFact = MessageFactory.newInstance();
		}
		return _msgFact;
	}

	private static SOAPConnection getSOAPConnection() throws SOAPException
	{
		if (_connectionSOAP == null) {
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			_connectionSOAP = soapConnectionFactory.createConnection();
		}
		return _connectionSOAP;
	}

	private static Transformer getXMLTransformer() throws TransformerConfigurationException
	{
		if (_xmlTransformer == null) {
			_xmlTransformer = TransformerFactory.newInstance().newTransformer();
			_xmlTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
		}
		return _xmlTransformer;
	}

	private static DocumentBuilder getDOMDocBuilder() throws ParserConfigurationException
	{
		if (_domBuilder == null) {
			 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			 factory.setNamespaceAware(true);
			_domBuilder = factory.newDocumentBuilder();
		}
		return _domBuilder;
	}

	/**
	 * Inserts an existing XML document as the payload of an existing SOAP message
	 * @param payload The XML payload to be inserted in a SOAP message
	 * @param messageSOAP The SOAP message
	 * @throws SOAPException SOAP API problem
	 * @throws TransformerException XSLT API problem
	 */
	public static void insertXMLPayloadInSOAPMessage(Document payload, SOAPMessage messageSOAP)
			throws SOAPException, TransformerException
	{
		SOAPBody body = messageSOAP.getSOAPBody();
		body.addDocument(payload); // WARNING: 'domObject' no longer valid after this call
	}

	/**
	 * Creates a new blank SOAP message.
	 * @return The blank SOAP message
	 * @throws SOAPException Sotware configuration problem
	 */
	public static SOAPMessage newBlankSOAPMessage() throws SOAPException
	{
		return getSOAPMsgFactory().createMessage();
	}

	/**
	 * Sends a SOAP request and retrieves the response (synchronously)
	 * @param message The SOAP request message
	 * @param url The URL of the web service to send the request to
	 * @return The SOAP response message
	 * @throws SOAPException Software configuration problem or server problem
	 */
	public static SOAPMessage sendSOAPRequest(SOAPMessage message, URL url) throws SOAPException
	{
		return getSOAPConnection().call(message, url);
	}

	/**
	 * Indicates if a SOAP message is actually a SOAP fault
	 * @param messageSOAP The SOAP message
	 * @return true if the SOAP message is a SOAP fault
	 */
	public static boolean isSOAPFault(SOAPMessage messageSOAP)
	{
		try {
			if (messageSOAP == null || messageSOAP.getSOAPBody().hasFault()) {
				return true;
			}
		}
		catch (SOAPException ignore) {
		}
		return false;
	}

	/**
	 * Retrieves the error message from a SOAP fault
	 * @param messageSOAP A SOAP message which is a SOAP fault
	 * @return The error message associated with the SOAP fault
	 */
	public static String getSOAPFaultString(SOAPMessage messageSOAP)
	{
		if (messageSOAP == null) {
			return "Response is blank or NULL";
		}
		try {
			return messageSOAP.getSOAPBody().getFault().getFaultString();
		}
		catch (SOAPException ex) {
			return "SOAPBody is NULL or empty";
		}
	}

	/**
	 * Extracts the payload from a SOAP message.
	 * @param messageSOAP The SOAP message
	 * @return The XML payload as a DOM document
	 * @throws Exception SOAP API problem
	 */
	public static Document extractXMLPayloadFromSOAPMessage(SOAPMessage messageSOAP) throws Exception
	{
		// Get the iterator of all child elements from sOAP body.
		for (Iterator iter=messageSOAP.getSOAPBody().getChildElements(); iter.hasNext(); ) {
			Object child = iter.next();
			if (child instanceof SOAPBodyElement) {
				// Import node in empty DOM document
				Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
				Node node = doc.importNode((SOAPBodyElement) child, true);
				doc.appendChild(node);
				return doc;
			}
		}
		return null;
	}

	/**
	 * Persists a DOM document to a file
	 * @param doc The DOM document
	 * @param filePath The persistence file path
	 * @throws IOException Wrong file path
	 * @throws TransformerException Software configuration error
	 */
	public static void writeDOMDocumentToFile(Document doc, String filePath) throws IOException, TransformerException
	{
		File outFile = new File(filePath);
		File outDir = new File(outFile.getParent());
		outDir.mkdir();

		FileOutputStream stream = null;
		try {
			stream = new FileOutputStream(outFile);
			writeDOMDocumentToStream(doc, stream);
		}
		finally {
			if (stream != null) {
				stream.close();
			}
		}
	}

	/**
	 * Serialises a DOM document to a stream.
	 * @param doc The DOM document
	 * @param stream The serialisation stream
	 * @throws TransformerException Software configuration error
	 */
	public static void writeDOMDocumentToStream(Document doc, OutputStream stream) throws TransformerException
	{
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(stream);
		getXMLTransformer().transform(source, result);
	}

	/**
	 * Serialises a DOM document to a string.
	 * @param doc The DOM document
	 * @return The serialisation string
	 * @throws Exception Software error
	 */
	public static String writeDOMDocumentToString(Document doc) throws Exception
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		writeDOMDocumentToStream(doc, stream);
		return stream.toString("UTF8");
	}

	/**
	 * Parses an XML stream into a DOM document.
	 * @param stream The XML stream to parse
	 * @return The DOM document resulting from parsing
	 * @throws Exception Software configuration problem or problem with underlying stream
	 */
	public static Document parseToDOM(InputStream stream) throws Exception
	{
		return getDOMDocBuilder().parse(stream);
	}

	/**
     * Creates a GZIP attachment to a SOAP message from an input data stream.
     *
     * @param messageSOAP SOAP message to add the attachment to
     * @param messageID A unique identifier for this attachment (amongst other attachments)
     * @param data The data to be compressed and included as attachment
     * @throws Exception IO problem
     */
    public static void createGZIPAttachment(SOAPMessage messageSOAP, String messageID, InputStream data) throws Exception
    {
        ByteArrayOutputStream compressedByteStream = new ByteArrayOutputStream();
        GZIPOutputStream compressedStream = new GZIPOutputStream(compressedByteStream);
        PrintWriter printWriterc = new PrintWriter(new OutputStreamWriter(compressedStream, "UTF8"));

        // read the stream and compress it, we must read it as UTF-8. dts0100807283
        InputStreamReader streamReader = new InputStreamReader(data, "UTF8");
        while (streamReader.ready())
        {
            char[] buffer = new char[1000];
            int numberOfReadBytes = streamReader.read(buffer, 0, 1000);
			if (numberOfReadBytes > 0) {
				printWriterc.write(buffer, 0, numberOfReadBytes);
			}
		}

        printWriterc.flush();
        compressedStream.finish();
        compressedStream.flush();
        ByteArrayInputStream result = new ByteArrayInputStream(compressedByteStream.toByteArray());

        DataHandler dh = new DataHandler(new InputStreamDataSource(result, SOAP_ATTACHMENT_FORMAT_GZIP));
        AttachmentPart attachment = messageSOAP.createAttachmentPart(dh);
        attachment.setContentId(messageID);

		messageSOAP.addAttachmentPart(attachment);
    }

	/**
	 * DataSource implementation for SOAP attachments
	 */
	private static class InputStreamDataSource implements DataSource
	{
        private InputStream m_inputStream;
        private String m_contentType;

        public InputStreamDataSource(InputStream is, String contentType) {
            m_inputStream = is;
            m_contentType = contentType;
        }

        public String getContentType() {
            return m_contentType;
        }

        public InputStream getInputStream() throws IOException {
            return m_inputStream;
        }

        public String getName() {
            return "Inputstream data source";
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Cannot write to this data source");
        }
    }

	/**
     * Creates an XML attachment to a SOAP message from an input data stream.
     *
     * @param messageSOAP SOAP message to add the attachment to
     * @param messageID A unique identifier for this attachment (amongst other attachments)
     * @param data The XML data to be included as attachment
	 */
	public static void createXMLAttachment(SOAPMessage messageSOAP, String messageID, InputStream data)
	{
		DataHandler dh = new DataHandler(new InputStreamDataSource(data, SOAP_ATTACHMENT_FORMAT_XML));
		AttachmentPart attachment = messageSOAP.createAttachmentPart(dh);
		attachment.setContentId(messageID);
		messageSOAP.addAttachmentPart(attachment);
	}

	/**
	 * Extracts an attachment from a SOAP messages and outputs it (uncompressed) to a stream.
	 * Assumes the message only includes one single attachment.
	 * @param messageSOAP The SOAP message with attachment
	 * @param outStream The stream to output the attachment to
	 * @throws Exception Unexpected type or count of attachment
	 */
	public static void extractDocumentFromSOAPAttachment(SOAPMessage messageSOAP, OutputStream outStream)
			throws Exception
	{
		// Assumes 1 single gzip attachment
		int attachCount = 0;

		for (Iterator iter = messageSOAP.getAttachments(); iter.hasNext(); ) {
			if (++attachCount == 1) {
				InputStream in = null;
				AttachmentPart attachment = (AttachmentPart) iter.next();
				if (SOAP_ATTACHMENT_FORMAT_GZIP.equals(attachment.getContentType()) ||
						SOAP_ATTACHMENT_FORMAT_OCTECT.equals(attachment.getContentType())) {
					in = (InputStream) attachment.getContent();
				}
				else if (SOAP_ATTACHMENT_FORMAT_XML.equals(attachment.getContentType())) {
					StreamSource source = (StreamSource) attachment.getContent();
					in = source.getInputStream();
				}
				else {
					throw new RuntimeException("Expected " + SOAP_ATTACHMENT_FORMAT_GZIP + " or "
							+ SOAP_ATTACHMENT_FORMAT_OCTECT
							+ " but received " + attachment.getContentType());
				}

				while (in.available() != 0) {
					byte[] buffer = new byte[1024];
					int readBytesCount = in.read(buffer, 0, 1024);
					if (readBytesCount > 0) {
						outStream.write(buffer, 0, readBytesCount);
					}
				}
			}
		}

		if (attachCount > 1) {
			throw new RuntimeException("Expected only one attachment, but received " + attachCount);
		}
	}
}
