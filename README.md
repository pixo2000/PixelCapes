# PixelCapes
Yeah just some Cosmetics for me and friends

## How to use
Just download the mod. Minecraft Version 1.21, Fabric Version 1.60.0
It needs the Fabric API Version 0.102.0+1.21

Then just join a Server and you should have a Cape

## What if you don't have a cape?
Maybe i didn't give you one. Just message me on Discord(Xandarian) and ask for a cape!

### What is planned?
A reload command / general Cape reloading
At this point, capes only reload on game restart



# ^^ ignore everything
# The Idea:
Client:<br>
Every user can see Capes, Deadmaus5 Ears and can be flipped. Just like on Minecraftcapes.net(also animated, more than normal resolution, glint etc) <br>
-> Resolution look at main branch the "Reload" Class <br>

Server:<br>
Is not a minecraft, but a webserver wich hosts a database with files like this: <br>
http://voidcube.de/capes/user/name.json
````
{
    "name": "name",
    "uuis": "uuid",
    "cape": "minecon2012",
    "deadmaus": "null",
    "flip": "true",
    "glint": "true",
    }
````

if a user has a cape, it displays the name. if not the name is null<br>
if a user has no cosmetic things(only name and uuid), the file should be delted

The file structure is like this:<br>
saving the users:<br>
voidcube.de/capes/user/

saving the capes:<br>
voidcube.de/capes/capes/foldername/capename.png

saving the ears:<br>
voidcube.de/capes/ears/foldername/earsname.png

example:<br>
voidcube.de/capes/capes/offical/minecon2012.png<br>
i could run /pixelcapes password set player cape /offical/minecon2012.png<br>
and then update everyone with /pixelcapes password update<br>
so <name> always means for example /offical/minecon2012.png<br>
if name is null the user has no cape/ears


There are also some commands for the mod:
````
/pixelcapes <password>  set player  glint           true/false
                                    flipped         true/false
                                    cape_texture    <name>
                                    ears_texture    <name>
                                    
                        upload  ears/cape           <link>          <name>
                        delete  ears/cape           <name>
                        get player
                        update
````

it should have autocomplete.<br>
the password is something like the current time(utc time) minus 10 minutes, so if it were 16:42 aka 4pm 42 the password would be 1632<br>
on every correct/uncorrect password, the user and the action is sent to a discord webhook<br>
the upload has an error if a cape in the folder already has the name and delete has a confirmation(have to repeat command in 5 seconds)<br>
It needs a connection between the server and the client, so if i run the update command, every client gets a command and refreshes the capes clientside<br>
the "get player" command just puts the http://voidcube.de/capes/user/name.json output in my chat<br>


no - there is no feature for the users to set their own capes, only i can do that