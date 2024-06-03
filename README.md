# JfgCdi

A simple CDI container to manage dependencies

## Import into eclipse

To import the project into eclipse:

* Clone it, but **not into the workspace** (for some reason eclipse can't import the project correctly if it's already in the workspace. A subdirectory of the workspace with a different name is ok, however)
* File -> import... -> Existing Gradle Project -> Configure a local Gradle version -> Next -> Finish

Note: The project will not be placed directly in the workspace but remains in the cloned directory (e.g. in a subdirectory of the workspace). Not ideal for console access, but I couldn't figure out how to do this in the current eclipse verision (2024/03).

## Examples

For examples on the usage of the library, see the test classes.

## Working with a CDI container

### Initialising a container

To use the CDI container it needs to be initialised with an array of packages, in which scoped classes are searched:
```
// load all annotated classes in all sub-packages of the given package names
CdiContainer.create("net.jfabricationgames.cdi", "net.yourProject.packageDeclaration"); 
```

### Injecting dependencies

To inject dependencies into a class you need to declare a field that is annotated with `@Inject` and which's type is a managed scope class:
```
public class DependentClass {
	
  /* ApplicationScopedBean needs to be a class that is annotated with either @ApplicationScoped or @InstanceScoped, 
   * and which was loaded by the CdiContainer.create method.
   *
   * NOTE: the injected object can have injected dependencies, that are resolved automatically.
   */
  @Inject
  private ApplicationScopedBean applicationScopedBean;

  public DependentClass() {
    CdiContainer.injectTo(this);
  }
}
```

### Scope types

#### @ApplicationScoped

ApplicationScoped classes are created only once for the whole application and are shared in every injection point. See [CdiContainerTest.testInjectApplicationScopedBean](https://github.com/tfassbender/JfgCdi/blob/f2f7be2b9f1a3be64d2b0fefe6fe3b898b783a6a/jfgCdi_lib/src/test/java/net/jfabricationgames/cdi/CdiContainerTest.java)

#### @InstanceScoped

InstanceScoped classes are created for every injection point so they are not shared. See [CdiContainerTest.testInjectInstanceScopedBeans](https://github.com/tfassbender/JfgCdi/blob/f2f7be2b9f1a3be64d2b0fefe6fe3b898b783a6a/jfgCdi_lib/src/test/java/net/jfabricationgames/cdi/CdiContainerTest.java)

#### Notes

- Cyclic dependencies are only allowed for `@ApplicationScoped` types.
- Local class needs to be declared as **public static**, to be injected into another classes.
- Interfaces of types can be used to achieve loose coupling. See [CdiContainerTest.testInjectIntoInterfaceFields](https://github.com/tfassbender/JfgCdi/blob/f2f7be2b9f1a3be64d2b0fefe6fe3b898b783a6a/jfgCdi_lib/src/test/java/net/jfabricationgames/cdi/CdiContainerTest.java)
- Injected classes can contain fields that are injected too. These fields are automatically filled when injecting the type. 
- If a new container is needed, the container can be destroyed with the `CdiContainer.destroy` method.

## Event handling

### Creating an Event Listener

An event listener can be any object, that declares at least one observer method. An observer method is a method with one parameter, that is annotated with @Observes. This method will be invoked with this parameter object whenever an event with this exact type is fired (supertype observers are not called):

```
private void handleEvent(@Observes SimpleEvent event) {
  // TODO handle the simple event
}
```

### Adding an Event Listener

To add an event listener, you need to get the EventHandler reference, by injecting it (it will be found automatically when initializing the CdiContainer):

```
@Inject
private EventHandler eventHandler;
```

Afterwards all event listeners can be added, using the `EventHandler.addListener(Object)` method. Note that all listeners must declare at least one method with a single parameter that is annotated with @Observes.

### Notes

- Observers of supertypes are not invoked when an subtype event is fired. Only methods with exact type matches are invoked.
- Observer methods must have exactly one parameter that is annotated with @Observes. Other parameters lead to an exception.
- An EventListener class can declare multiple observer methods, with the same observed type and / or different observed types.
- The EventHandler is found automatically when creating the CdiContainer. There is no need to add it's package name when creating the CdiContainer.
- Observer methods are inherited.
- The removeListener method of the EventHandler compares instances. Equal objects will not be removed.
