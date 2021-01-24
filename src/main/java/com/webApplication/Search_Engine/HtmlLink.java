package com.webApplication.Search_Engine;

/**
 * This class implements an html link
 */
public class HtmlLink {
    //an html link consists of the link name and the whole linkText = http://...
    String link;
    String linkText;

    HtmlLink(){};

    @Override
    public String toString() {
        return new StringBuffer("Link : ").append(this.link)
                .append(" Link Text : ").append(this.linkText).toString();

    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = replaceInvalidChar(link);
    }

    public String getLinkText() {
        return linkText;
    }

    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }
    //This function replaces every invalid char of the link with blank
    public static String replaceInvalidChar(String link){
        link = link.replaceAll("'", "");
        link = link.replaceAll("\"", "");
        return link;
    }

}
