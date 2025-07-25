//wap to find volume of cuboid using user defined function.
import java.util.*;

class Cuboid{
public int volume(int l,int b,int h){
 return(l*b*h);
}

public static void main(String[]args){


int x,y,z,v;

Scanner sc= new Scanner(System.in);
System.out.println("Enter the length of cuboid");
x=sc.nextInt();
System.out.println("Enter the width of cuboid");
y=sc.nextInt();
System.out.println("Enter the height of cuboid");
z=sc.nextInt();
Cuboid cu = new Cuboid();
v=cu.volume(x,y,z);
System.out.println("volume = "+v);
}
}