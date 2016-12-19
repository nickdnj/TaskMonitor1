/*
 * Copyright 2007 Mentor Graphics Corporation. All Rights Reserved.
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

import javax.mail.Session;
import java.util.Properties;

/**
 * The main program to invoke a CIS web service from a client application
 */
public class ClientMain
{
	public static void main(String[] args)
	{
		try {
			boolean loggingMode = false;
			String clientClassName = null;
			String clientParam = null;
			boolean argError = false;

			// Parse arguments
			for (int i=0; !argError && i<args.length; i++) {
				//System.out.println("arg " + Integer.toString(i) + ": " + args[i]);
				if (args[i].equalsIgnoreCase("-l")) {
					loggingMode = true;
				}
				else {
					if (clientClassName!=null && clientParam!=null) {
						argError = true;
					}
					else if (clientClassName==null) {
						clientClassName = args[i];
					}
					else {
						clientParam = args[i];
					}
				}
			}

			if (argError) {
				System.err.println("Error: Invalid arguments");
			}
			else {
				if (clientClassName == null) {
					clientClassName = "ListTaskClient";
					//clientClassName = "DescribeTaskClient";
				}

				// Add package name if not there
				if (clientClassName.indexOf('.') <= 0) {
					//clientClassName = "com.example.webservice.client." + clientClassName;
					clientClassName = clientClassName;
				}

				// Instantiate the client
				Class clientClass = Class.forName(clientClassName);
				AbstractClient client = (AbstractClient) clientClass.newInstance();

				// Invoke

				client.invoke(loggingMode, clientParam);
			}
		}

		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
