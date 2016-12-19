/*
 * Copyright 2011 Mentor Graphics Corporation. All Rights Reserved.
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
//package com.example.webservice.client;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.soap.SOAPMessage;
import java.io.FileOutputStream;

/**
 * A concrete implementation for the CIS "DescribeTask" web service client,
 * using the SOAP with attachments format
 */
public class DescribeTaskClient extends AbstractClient
{
//	public String TaskId = "UID0859c5-1577c29b98d-f528764d624db129b32c21fbca0cb8d6";


	private String TaskID;

	public DescribeTaskClient() {
	}

	public DescribeTaskClient(String id) {
		this.TaskID = id;
	}


	protected String getWebServiceName()
	{
		return "DescribeTask";
	}

	protected String getRequestPayload()
	{
		//return "<taskspec attachments='false' executions='true' id='UIDdd9b69-15776fb64da-f528764d624db129b32c21fbca0cb8d6' parameters='true'/>";
		//String TaskId = "UID0859c5-1577c29b98d-f528764d624db129b32c21fbca0cb8d6";
		return "<taskspec attachments='false' executions='true' id=\"" + this.TaskID + "\" parameters='true'/>";
	}

	protected boolean isResponseExcepted()
	{
		return true;
	}

	protected boolean hasResponseAttachments()
	{
		return true;
	}

	protected void processResponseAttachments(SOAPMessage messageSOAP) throws Exception
	{
		// Retrieve the attachment and persist it to a file
		String filePath = OUTPUT_DIRECTORY + "/parameters.xml";
		FileOutputStream outStream = new FileOutputStream(filePath);
		WebServiceUtils.extractDocumentFromSOAPAttachment(messageSOAP, outStream);
		outStream.close();
		System.out.println("XML design as attachment successfully written to: " + filePath);
	}

	protected void processResponse(Document responsePayload) throws Exception
	{
		Element tasks = responsePayload.getDocumentElement();
		System.out.println(responsePayload.getTextContent());
		
		// Retrieve task description
		NodeList taskNodes = tasks.getElementsByTagName("taskspec");
		int nbNodes = taskNodes.getLength();
		for (int i=0; i<nbNodes; i++) {
			System.out.println();
			Element taskspec = (Element) taskNodes.item(i);
			System.out.println("> Instance name: " + taskspec.getAttribute("instance_name"));
			System.out.println("> Name: " + taskspec.getAttribute("name"));
			System.out.println("> Cron expression: " + taskspec.getAttribute("cron_expression"));
			System.out.println("> Status: " + taskspec.getAttribute("status"));
			System.out.println("> Submit time: " + taskspec.getAttribute("submit_time"));
			System.out.println("> User Name: " + taskspec.getAttribute("username"));
			
		}
		
		// Retrieve task executions
		NodeList execNodes = tasks.getElementsByTagName("taskexec");
		nbNodes = execNodes.getLength();
		for (int i=0; i<nbNodes; i++) {
			System.out.println();
			Element exec = (Element) execNodes.item(i);
		
			System.out.println("> id: " + exec.getAttribute("id"));
			System.out.println("> Occurence id: " + exec.getAttribute("occurr_id"));
			System.out.println("> Progress text: " + exec.getAttribute("progress_text"));
			System.out.println("> Progress %: " + exec.getAttribute("progress_perc"));
			System.out.println("> Run status: " + exec.getAttribute("runstatus"));
			System.out.println("> Run time: " + exec.getAttribute("run_time"));
			System.out.println("> Start time: " + exec.getAttribute("start_time"));
			System.out.println("> End time: " + exec.getAttribute("end_time"));
		}
	}
}
