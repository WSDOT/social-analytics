/*
 * Copyright (c) 2016 Washington State Department of Transportation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package gov.wa.wsdot.apps.analytics.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public interface Resources extends ClientBundle {
	public static final Resources INSTANCE = GWT.create(Resources.class);
	
	  @Source("GlobalStyles.css")
	  public Styles css();
	  
	  @Source("bullet.gif")
	  ImageResource bulletGIF();
	  
	  @Source("WSDOTacronymWhite.png")
	  ImageResource tacronymWhiteLogoPNG();
	  
	  @Source("89.gif")
	  ImageResource ajaxLoaderGIF();
	  
	  @Source("ajaxloader.gif")
	  ImageResource ajaxLoader2GIF();
	  
	  /*
	  @Source("positive.png")
	  ImageResource positivePNG();

	  @Source("negative.png")
	  ImageResource negativePNG();
	  */
	  
	  public interface Styles extends CssResource {
		  // Classes
		  String clearLeft();
		  String clearRight();
		  String clearBoth();
		  String header();
		  String topban();
		  String topnav();
		  String first();
		  String logo();
		  String search();
		  String searchText();
		  String wrapper();
		  String leftnav();
		  String leftnavbox();
		  String main();
		  String footer();
		  String bottomnav();
		  String ajaxLoading();
		  String ajaxLoading2();
		  //String positive();
		  //String negative();
		  //String neutral();
		  String tweetBox();
		  String sectionHeader();
		  String sectionHeaderRight();
		  String labelTest();
		  String tweetIcon();
	  }	
}
