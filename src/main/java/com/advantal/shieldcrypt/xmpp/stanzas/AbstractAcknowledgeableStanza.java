package com.advantal.shieldcrypt.xmpp.stanzas;

import com.advantal.shieldcrypt.xml.Element;
import com.advantal.shieldcrypt.xmpp.InvalidJid;

abstract public class AbstractAcknowledgeableStanza extends AbstractStanza {

    protected AbstractAcknowledgeableStanza(String name) {
        super(name);
    }


    public String getId() {
        return this.getAttribute("id");
    }

    public void setId(final String id) {
        setAttribute("id", id);
    }

    private Element getErrorConditionElement() {
        final Element error = findChild("error");
        if (error == null) {
            return null;
        }
        for (final Element element : error.getChildren()) {
            if (!element.getName().equals("text")) {
                return element;
            }
        }
        return null;
    }

    public String getErrorCondition() {
        final Element condition = getErrorConditionElement();
        return condition == null ? null : condition.getName();
    }

    public boolean valid() {
        return InvalidJid.isValid(getFrom()) && InvalidJid.isValid(getTo());
    }
}
