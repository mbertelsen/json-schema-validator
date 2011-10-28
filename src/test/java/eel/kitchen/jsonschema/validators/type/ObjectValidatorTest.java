/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eel.kitchen.jsonschema.validators.type;

import eel.kitchen.jsonschema.validators.Validator;
import eel.kitchen.util.JasonHelper;
import org.codehaus.jackson.JsonNode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.testng.Assert.*;

public class ObjectValidatorTest
{
    private JsonNode testNode, node;
    private final Validator v = new ObjectValidator();
    private boolean ret;
    private List<String> messages;

    @BeforeClass
    public void setUp()
        throws IOException
    {
        testNode = JasonHelper.load("object.json");
    }

    @Test
    public void testRequired()
    {
        node = testNode.get("required");
        v.setSchema(node.get("schema"));

        ret = v.setup();

        assertTrue(ret);

        ret = v.validate(node.get("bad"));
        messages = v.getMessages();

        assertFalse(ret);
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "property p1 is required but was not found");

        ret = v.validate(node.get("good"));
        messages = v.getMessages();

        assertTrue(ret);
        assertTrue(messages.isEmpty());
    }

    @Test
    public void testNoAdditional()
    {
        node = testNode.get("noAdditional");
        v.setSchema(node.get("schema"));

        ret = v.setup();

        assertTrue(ret);

        ret = v.validate(node.get("bad"));
        messages = v.getMessages();

        assertFalse(ret);
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "additional properties were found but " +
            "schema forbids them");

        ret = v.validate(node.get("good"));
        messages = v.getMessages();

        assertTrue(ret);
        assertTrue(messages.isEmpty());
    }

    @Test
    public void testDependencies()
    {
        node = testNode.get("dependencies");
        v.setSchema(node.get("schema"));

        ret = v.setup();

        assertTrue(ret);

        ret = v.validate(node.get("bad"));
        messages = v.getMessages();

        assertFalse(ret);
        assertEquals(messages.size(), 1);
        assertEquals(messages.get(0), "property p1 depends on p3, "
            + "but the latter was not found");

        ret = v.validate(node.get("good"));
        messages = v.getMessages();

        assertTrue(ret);
        assertTrue(messages.isEmpty());
    }
}
