# callcenter
Propuesta de solucion al ejercicio de callcenter utilizando Java y programacion Orientada a Objetos

## Descripcion General
En este repositorio se presenta una aplicacion de consola utilizando Java. Para su ejecucion se requiere Java version 1.8.
Para su compilación se requiere Apache Maven version 3.

```bash
mvn compile ## para compilar la aplicacion. Por defecto se ejecutan las pruebas unitarias

mvn package ## para generar el archivo ejecutable .jar
```

## Diseño y organizacion
La organizacion del codigo se describe a continuacion:
* Paquete [businesslogic](https://github.com/lincex7845/callcenter/tree/master/src/main/java/com/mera/callcenter/businesslogic):

  * EmployeePredicates: logica de dominio para determinar el tipo de empleado y disponibilidad del mismo.

  * HandleCallStrategy: logica de dominio que permite determinar como deben asignarse las llamadas a los empleados de acuerdo a su disponibilidad y jerarquia. Tambien como deben manejarse aquellas llamadas que son puestas en espera, mientras los empleados atienden las llamadas. Es una implementacion del patron de comportamiento _Strategy_.

* Paquete [entities](https://github.com/lincex7845/callcenter/tree/master/src/main/java/com/mera/callcenter/entities):

  * Call: representa una llamada y su duracion.
  
  * EmployeeType: enumeracion para representar el tipo de empleado o cargo dentro de la jerarquia. Los tipos son:
  ```java
  
  OPERATOR,     // operador
  SUPERVISOR,   // supervisor
  MANAGER       // director
  ```
  
  * EmployeeStatus: enumeracion que representa la disponibilidad de un empleado para atender una llamada. Los diferentes tipos de  disponibilidades son:
  ```java
  AVAILABLE, // disponible para atender la llamada
  ON_CALL,   // tiene una llamada asignada pero no la ha atendido aun
  BUSY       // tiene una llamada asignada y la esta atendiendo
  ```
  
  * Employee: representa la persona que atiende la llamada. [Implementa la interfaz Runnable](https://github.com/lincex7845/callcenter/blob/master/src/main/java/com/mera/callcenter/entities/Employee.java#L56) con el fin de atender la llamada. Esto se simula pausando la ejecucion del hilo (sleep) durante el tiempo que dura la llamada.
  
 * [Dispatcher](https://github.com/lincex7845/callcenter/blob/master/src/main/java/com/mera/callcenter/Dispatcher.java): clase encargada de la ejecucion del programa, por tanto, la asignacion de las llamadas a los empleados.
 Provee el metodo [dispatchCall](https://github.com/lincex7845/callcenter/blob/master/src/main/java/com/mera/callcenter/Dispatcher.java#L19) que al estar notado como **synchronized** puede ejecutarse de forma segura (thread-safe) desde distintos hilos de forma concurrente.
 
 * [CallCenterFactory](https://github.com/lincex7845/callcenter/blob/master/src/main/java/com/mera/callcenter/CallCenterFactory.java): clase utilitaria que permite crear instancias de las diferentes entidades del call center. Es una implementacion del patron _Factory_, que sirve como interfaz comun para la creacion de una o varias instancias de una entidad del sistema.
 
 ## Extras/Plus
 
 * La clase Dispatcher dispone de una [cola](https://github.com/lincex7845/callcenter/blob/master/src/main/java/com/mera/callcenter/Dispatcher.java#L26) de acceso seguro en concurrencia. En caso de que no existan empleados disponibles la llamada se coloca en espera dentro de esta cola hasta que un empleado este disponible.
 * El mismo procedimiento se aplica cuando entran mas de 10 llamadas al mismo tiempo. 
 Aunque el Dispatcher tiene la capacidad de procesar hasta maximo 10 llamadas, la nueva llamada es colocada en espera si no encuentra ningun empleado disponible, sino se asignara al primer empleado disponible
 * La suite [DipatcherTest](https://github.com/lincex7845/callcenter/blob/master/src/test/java/com/mera/callcenter/DispatcherTest.java) provee pruebas unitarias para verificar los comportamientos mencionados anteriormente.
