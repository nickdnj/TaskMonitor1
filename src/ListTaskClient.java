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
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import java.util.Properties;
import javax.mail.Session;



/**
 * A concrete implementation for the CIS "ListTasks" web service client.
 */
public class ListTaskClient extends AbstractClient
{
	public ListTaskClient() throws Exception
	{
	}


	protected String getWebServiceName()
	{
		return "ListTasks";
	}

	protected String getRequestPayload()
	{
		//return "<criteria><names> <name value='LibraryExportTask'/></names><statuses> <status value='FAILED'/> </statuses></criteria>";
		//return "<criteria></criteria>";


		return "<criteria>" +
				"<names>" +
				"<name value='LibraryExportTask'/>" +
				"<name value='LibraryImportTask'/>" +
				"<name value='SymbolLibraryExportTask'/>" +
				"<name value='SymbolLibraryImportTask'/>" +
				"<name value='DesignExportTask'/>" +
				"<name value='DesignImportTask'/>" +
				"</names>" +
				"</criteria>";

	}
	

	protected boolean isResponseExcepted()
	{
		return true;
	}

	protected void processResponse(Document responsePayload) throws Exception
	{
		Element tasks = responsePayload.getDocumentElement();

		// Retrieve task nodes
		NodeList nodes = tasks.getElementsByTagName("taskspec");
		int nbNodes = nodes.getLength();
		System.out.println();
		System.out.println("Tasks: " + nbNodes);
		for (int i=0; i<nbNodes; i++) {
			System.out.println();
			Element taskspec = (Element) nodes.item(i);
			//System.out.println("> Instance name: " + taskspec.getAttribute("instance_name"));
			//System.out.println("> Name: " + taskspec.getAttribute("name"));
			//System.out.println("> Cron expression: " + taskspec.getAttribute("cron_expression"));
			//System.out.println("> Status: " + taskspec.getAttribute("status"));
			//System.out.println("> Submit time: " + taskspec.getAttribute("submit_time"));
			//System.out.println("> User Name: " + taskspec.getAttribute("username"));
			//System.out.println("> ID: " + taskspec.getAttribute("id"));
			//**************************************************************************************************************
			DescribeTaskClient taskClient = new DescribeTaskClient(taskspec.getAttribute("id"));
			taskClient.invoke(true);



			//**************************************************************************************************************
		}
	}
}
