package com.advantal.shieldcrypt.utils_pkg;

import org.jivesoftware.smack.packet.ExtensionElement;

public class CustomExtensionModel implements ExtensionElement {

    String Element;
    String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

//    String name = "client_receipt";

    public String getElement() {
        return Element;
    }

    public void setElement(String element) {
        Element = element;
    }
//    String namespace = "client_receipt";

    @Override
    public String getElementName() {
        return Element;
    }

    @Override
    public String getNamespace() {
        return null;
    }

    @Override
    public CharSequence toXML(String enclosingNamespace) {
//        XmlStringBuilder xml = new XmlStringBuilder(this);
        String str = String.format("<client_receipt>%s</client_receipt>", text);
        return str;
    }
}