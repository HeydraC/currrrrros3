import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.io.IOException;
import java.util.Random;

public class Proyecto3{
	public static void main(String[] args){
		Vehiculo[] vehiculos = null;
		int[] baterias = null;
		UnidadCarga[] cargadores = null;
		Estacionamiento estacionamiento = null;
		Scanner s = new Scanner(System.in);

		System.out.print("Nombre del archivo TXT: ");
		String archivo = s.nextLine();

		boolean[][] tableroOriginal = {
			{false, false, false, false, false, false},
			{false, false, false, false, false, false},
			{false, false, false, false, false, false},
			{false, false, false, false, false, false},
			{false, false, false, false, false, false},
			{false, false, false, false, false, false}
		}; //Todas las casillas empiezan sin ocupar

		try {
		    ArrayList<String> lineas = new ArrayList<>(Files.readAllLines(Paths.get(archivo + ".txt")));

		    vehiculos = new Vehiculo[lineas.size() - 1];
		    baterias = new int[lineas.size() - 1];

		    for (int i = 0; i < lineas.size() - 1; ++i) {
		    	String[] info = lineas.get(i).split(",");

		    	int id, longitud, x, y, bat;
		    	char orientacion;

		    	id = Integer.parseInt(info[0].trim());
		    	orientacion = info[1].trim().charAt(0);
		    	x = Integer.parseInt(info[2].trim());
		    	y = Integer.parseInt(info[3].trim());
		    	longitud = Integer.parseInt(info[4].trim());
		    	baterias[i] = Integer.parseInt(info[5].trim());

		    	vehiculos[i] = new Vehiculo(id, orientacion, longitud, x, y, tableroOriginal);
		    }

		    String[] info = lineas.get(lineas.size() - 1).split(",");
		    cargadores = new UnidadCarga[Integer.parseInt(info[1].trim())];
		} catch (IOException e) {
		    e.printStackTrace();
		}

		estacionamiento = new Estacionamiento(tableroOriginal, baterias);

		for (int i = 0; i < vehiculos.length; ++i){
			vehiculos[i].est(estacionamiento);
			vehiculos[i].start();
		}
		for (int i = 0; i < cargadores.length; ++i){
			cargadores[i] = new UnidadCarga(estacionamiento);
			cargadores[i].start();
		}

		s.close();
	}
}

class Estacionamiento{
	boolean[][] tablero;
	int[] baterias;
	boolean terminado;
	Random r;

	public Estacionamiento(boolean[][] tableroOriginal, int[] _baterias){
		tablero = tableroOriginal;
		baterias = _baterias;
		terminado = false;

		r = new Random();
	}

	public synchronized void mover(int id, int orientacion, int longitud, int[] coords){
		int dir = r.nextBoolean() ? 1 : -1;

		while (baterias[id] == 0){
			try { wait();
			} catch( InterruptedException e ) { ; }
		}

		int check = dir > 0 ? longitud : -1;
		
		//Intenta avanzar un espacio
		if (orientacion == 'h'){
			if (coords[0] + check < 0 || coords[0] + check > 5){
				notifyAll();
				return;
			}
			if (tablero[coords[1]][coords[0] + check]){
				notifyAll();
				return;
			}

			if (dir > 0) tablero[coords[1]][coords[0]] = false;
			else tablero[coords[1]][coords[0] + longitud - 1] = false;
		}else{
			if (coords[1] + check < 0 || coords[1] + check > 5){
				notifyAll();
				return;
			}
			if (tablero[coords[1] + check][coords[0]]){
				notifyAll();
				return;
			}

			if (dir > 0) tablero[coords[1]][coords[0]] = false;
			else tablero[coords[1] + longitud - 1][coords[0]] = false;
		}


		if (orientacion == 'h'){
			tablero[coords[1]][coords[0] + check] = true;
			coords[0] += dir;
		}else{
			tablero[coords[1] + check][coords[0]] = true;
			coords[1] += dir;
		}
		
		baterias[id] -= 1;

		if (id == 0 && coords[0] == 6 - longitud){
			terminado = true;
			System.out.println("El vehículo 0 llegó a la salida");
			System.out.println("Estado final: ");
		}

		notifyAll();
	}

	public synchronized void cargar(){
		for (int i = 0; i < baterias.length; ++i){
			//Si hay un vehiculo detenido lo recarga
			if (baterias[i] == 0){
				baterias[i] = 10;
				break;
			}
		}

		notifyAll();
	}

	public synchronized boolean terminado(){
		notifyAll();
		return terminado;
	}
}

class Vehiculo extends Thread{
	int id;
	char orientacion;
	int longitud;
	int x, y;
	int bateria;
	Estacionamiento e;

	//Como los vehículos se crean de forma secuencial al leer el archivo, se accede directamente al tablero
	public Vehiculo(int _id, char _orientacion, int _longitud, int _x, int _y, boolean[][] tablero){
		id = _id;
		orientacion = _orientacion;
		longitud = _longitud;
		x = _x;
		y = _y;

		if (orientacion == 'h'){
			for (int i = 0; i < longitud; ++i){
				if (tablero[y][x + i]){
					System.out.println("Dos vehículos chocan en la posición inicial");
					System.exit(0);
				}
				tablero[y][x + i] = true;
			}
		}else{
			for (int i = 0; i < longitud; ++i){
				if (tablero[y + i][x]){
					System.out.println("Dos vehículos chocan en la posición inicial");
					System.exit(0);
				}
				tablero[y + i][x] = true;
			}
		}
	}

	public void est(Estacionamiento _e){
		e = _e;
	}

	public void run(){
		int[] coords = {x, y};

		while(!e.terminado()){
			e.mover(id, orientacion, longitud, coords);
			x = coords[0];
			y = coords[1];
		}

		System.out.println("Vehículo " + id + ": (" + x + ", " + y + ")");
	}
}

class UnidadCarga extends Thread{
	Estacionamiento e;

	public UnidadCarga(Estacionamiento _e){
		e = _e;
	}

	public void run(){
		while(!e.terminado()){
			e.cargar();
		}
	}
}