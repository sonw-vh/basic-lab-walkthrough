# LAB 3 WALKTHROUGH: DECENTRALIZATION VULNERABILITY

## Define the target
There's a flag hidden in this form as `Flag.txt`:
```
TTHL{L0nG_L1v3_Ins3cUr3_dEser1al1zatiOn}
```
In the source code, we found the function that stores this file:

![1.png](/img/1.png)

What we have to do is to output the flag on the website.

## Investigate
Here's the form page:

![2.png](/img/2.png)

Let's input something challenging like:

![png](/img/3.png)

Enter and we get this:

![png](/img/4.png)

Seems like only the email field has some kind of verify function, let's check the source code:

![png](/img/5.png)

And yes, there's only a whitelist filter for the email field. That means our following steps don't have to touch the other fields. 

Let's look for the serialize stuff:

![png](/img/6.png)

There's a `urlencode` function for the address field, which means this field make some mysterious role here. It also has a regex function here:

```php
$condition = preg_match('/%22|%7C|%23|%26|%5E|%27|%29|%28/', $object->address );
```

- `%22`: represents the ASCII code for the double quote (`"`).
- `%7C`: represents the ASCII code for the pipe symbol (`|`).
- `%23`: represents the ASCII code for the pound or hash symbol (`#`).
- `%26`: represents the ASCII code for the ampersand symbol (`&`).
- `%5E`: represents the ASCII code for the caret symbol (`^`).
- `%27`: represents the ASCII code for the single quote (`'`).
- `%29`: represents the ASCII code for the closing parenthesis (`)`).
- `%28`: represents the ASCII code for the opening parenthesis (`(`).

Let's try input all those above to test this regex!

![png](/img/7.png)

### What cause this?

When the input matches the regex pattern in the
`$condition = preg_match('/%22|%7C|%23|%26|%5E|%27|%29|%28/', $object->address );`, the `elseif` statement will be triggered and sets the `$suggest` variable to the serialized object.

Finally, the `contact` function is called with the `$serialize_object` variable, which contains the serialized object. The `unserialize_object()` function then tries to unserialize the serialized string, which results in the string being outputted as HTML:
```
 OBJECTION|O:7:"contact":4:{s:5:"email";s:13:"test@test.com";s:4:"name";s:0:"";s:5:"phone";s:0:"";s:7:"address";s:3:"%23";}
```

### OBJECT INJECTION VULNERABILITY

```php
$object->address  =  urlencode($_POST['contact_address']);
$serialize_object = "OBJECTION|".serialize($object);
```

Since the serialized string contains the `OBJECTION|` prefix, the resulting associative array will contain a key with the value `OBJECTION`. The corresponding value will be the unserialized contact object, which will have its `address` property set to the URL encoded value of `#`.

![png](/img/8.png)

The `Example` class has a `__wakeup()` function, a PHP `magic method`, that gets called when an object of this class is unserialized. The `__wakeup()` method checks if the `$hook` property of the object is set, and if it is, it reads the flag from a file and prints it out.

With the information above, we can determine that there is an PHP object injection vulnerability can be executed in the `__wakeup()` function.

## Vulnerability exploit

To trigger the execution of the `__wakeup()` method and print out the flag, we need to create an object of the `Example` class, set the `$hook` property to a non-empty value, serialize the object, and then pass the serialized object to the `contact` function, specifically the `address` field.

![png](/img/9.png)

There we go, we are now seeing the content of the `Flag.txt`.