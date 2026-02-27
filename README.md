# Recursos críticos
Se identificaron 2 recursos críticos: El tablero y las baterías de los vehículos. Estos recursos son accedidos mediante el monitor Estacionamiento, y además se agregó un booleano que avisa a los vehículos que 
se liberó al vehículo 0.

El tablero se implementó como una matriz de booleanos, donde una posición es *true* si está ocupada por un vehículo, las baterías de los vehículos se implementaron como un arreglo de enteros al que cada vehículo 
accede mediante su id.

# Operaciones sobre el monitor
Se definieron 3 métodos synchronized: mover, cargar y terminado.

**Mover** es usado por los vehículos, estos revisan si tienen batería para moverse, eligen una dirección aleatoria y se mueven una posición si el espacio está libre y dentro del tablero. 
En caso de ser el vehículo 0, este revisa si ya fue liberado para avisar a los demás hilos.

**Cargar** es usado por las unidades de carga, estas iteran el arreglo de baterías para revisar si hay un vehículo descargado y en caso de encontrarlo aumentan su batería a 10 (Se tomó el valor de ejemplo del enunciado).

**Terminado** es usado por los vehículos para acceder al booleano mediante el que se comprueba si ya se liberó al vehículo 0.

Se asegura la exclusión mútua al usar métodos synchronized definidos en el monitor Estacionamiento y se evitan interbloqueos retornando cuando se intenta mover a una posición inválida, permitiendo esto volver a intentar en otra dirección o dar el turno a otro vehículo para moverse.

# Instrucciones de uso
Al ejecutar el proyecto este pedirá el nombre del archivo .txt, este deberá ser escrito sin el .txt. Es decir: si se llama "test.txt" se deberá escribir "test", el programa se encarga de agregar la extensión.

El programa avisará y terminará en caso de que haya una colisión o vehículos fuera del tablero en el estado inicial.

Igualmente lo hará en caso de no definir un vehículo con id 0 o no definir cargadores.

Si se encuentra una solución cada vehículo imprimirá su posición final antes de terminar el programa.

**IMPORTANTE:** los vehículos acceden al arreglo de baterías mediante su id, asegurarse de definir los vehículos con id que vayan de 0 a n-1. No importa el orden de los vehículos en el .txt.
