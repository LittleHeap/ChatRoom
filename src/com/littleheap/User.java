package com.littleheap;

import javax.swing.*;
import com.littleheap.Service.ChatThread;
import java.net.*;
import java.util.ArrayList;
import java.awt.*;
import java.io.*;
import java.awt.event.*;

class User extends JFrame{
	ArrayList<Users> users = new ArrayList<Users>();
	Socket s;
	String userName;
	static String filename;
	String choice;
	JTextField jtf = new JTextField();
	JTextArea jta = new JTextArea();
	JComboBox jcb = new JComboBox();
	JButton jbt = new JButton();
	JButton jbt_data = new JButton();
	PrintStream ps;
	BufferedReader br;
	User() throws Exception{
		users.add(new Users("Ⱥ��"));
		//////////������ǰ��/////////////
		getContentPane().setForeground(Color.MAGENTA);
		getContentPane().setBackground(Color.WHITE);
		this.setSize(350,460);
		this.setResizable(false);
		setLocationRelativeTo(null); 
		getContentPane().setLayout(null);
		this.setVisible(true);
		/////////jtfǰ��//////////////////
		jtf= new JTextField();
		jtf.setFont(new Font("����", Font.PLAIN, 22));
		jtf.setBounds(0, 383, 252, 37);
		getContentPane().add(jtf);
		jtf.setColumns(10);
		///////jtaǰ��//////////////////
		jta= new JTextArea();
		jta.setFont(new Font("Monospaced", Font.PLAIN, 17));
		jta.setBackground(new Color(255, 255, 255));
		jta.setBounds(0, 32, 344, 345);
		getContentPane().add(jta);
		///////jcbǰ��////////////////////
		jcb = new JComboBox();
		jcb.setBounds(0, 0, 196, 29);
		getContentPane().add(jcb);
		///////���߰�ťǰ��/////////////
		jbt = new JButton("\u4E0B\u7EBF");
		jbt.setFont(new Font("��Բ", Font.PLAIN, 17));
		jbt.setForeground(Color.BLUE);
		jbt.setBackground(new Color(204, 255, 102));
		jbt.setBounds(254, 383, 90, 37);
		getContentPane().add(jbt);
		///////�ļ���ťǰ��/////////////
		jbt_data = new JButton("\u53D1\u9001\u6587\u4EF6");
		jbt_data.setFont(new Font("����", Font.PLAIN, 20));
		jbt_data.setBackground(new Color(102, 204, 204));
		jbt_data.setBounds(198, 0, 146, 29);
		getContentPane().add(jbt_data);
		//////////////////�����˺�///////////////////
		userName = JOptionPane.showInputDialog("�����ǳ�");
		this.setTitle(userName);
		//����Socket
		s = new Socket("127.0.0.1",9999);		
		ps = new PrintStream(s.getOutputStream());
		br = new BufferedReader(new InputStreamReader(s.getInputStream()));
		ps.println("LOGIN#"+userName);//���ǳƸ�������
		//������ActionListener
		jtf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {									
				int i =0;
				ps.println(choice+"#"+userName + "˵��" + jtf.getText());
				if(!choice.equals("Ⱥ��")){
					for(i=0;i<users.size();i++){
						if(users.get(i).getName().equals(choice)) break;
					}
					users.get(i).setContext(users.get(i).getContext()+userName + "˵��" + jtf.getText()+"\n");
				}
				jtf.setText("");
			}
		});
		//�����ļ��İ�ť��Ӧ
		jbt_data.addActionListener(new ActionListener() {							
			public void actionPerformed(ActionEvent arg0) {
				if(jbt_data.getText().equals("�����ļ�")){
					String[] file_name = jtf.getText().split("\\/");	
					for(int i=0;i<users.size();i++){
						if(users.get(i).name.equals(choice))
							users.get(i).setContext(users.get(i).getContext()+"����"+choice+"�������ļ���"+file_name[file_name.length-1]+"\n");
					}
					ps.println("�����ļ�"+"#"+userName+"#"+choice+"#"+file_name[file_name.length-1]);
					System.out.println("�����ļ�"+"#"+userName+"#"+choice+"#"+file_name[file_name.length-1]);
					System.out.println(file_name[file_name.length-1]);
					try {
						Thread.sleep(1000);
						send_file(jtf.getText());
					} catch (Exception e) {	e.printStackTrace();   }										
				}else{
					try {
						for(int i=0;i<users.size();i++){
							if(users.get(i).getName().equals(choice))
								users.get(i).setContext(users.get(i).getContext()+"���ѽ����ļ���"+filename);
						}
						receiveFile(jtf.getText()+filename);
					} catch (IOException e) {
						e.printStackTrace();
					}
					jbt_data.setText("�����ļ�");
				}
			}
		});
		//���߰�ť��ActionListener
		jbt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for(int i=0;i<users.size();i++){
					if(users.get(i).getName().equals(userName)){
						users.remove(i);
						break;
					}
				}
				ps.println("����"+"#"+userName);
				try { s.close(); } catch (IOException e) {e.printStackTrace();}//�ر�socket
				dispose();
			}
		});
		//ʵʱ��ȡ��Ϣ�߳�
		new Thread(new Runnable() {
			public void run() {
				while(true){
					try{
						String massage= br.readLine();
						String[] strs = massage.split("#");
						if(strs[0].equals("LOGOUT")){
							s.close();
							dispose();
						}else if(strs[0].equals("LOGIN")){
							int j=0;
							jcb.removeAllItems();
							for(int i=1;i<strs.length;i++){
								if(!strs[i].equals(userName)) { jcb.addItem(strs[i]); }
								for(j=0;j<users.size();j++){
									if(users.get(j).name.equals(strs[i]))  break;
								}
								if(j==users.size()&&!strs[i].equals(userName)) users.add(new Users(strs[i]));
							}
							jcb.addItem("Ⱥ��");
						}else if(strs[0].equals("�����ļ�")){
							jbt_data.setText("�����ļ�");
							filename = strs[1];
						}else{
							String[] str = strs[1].split("\\:");
							if(!str[0].equals(userName+"˵")){
								if(strs[0].equals("Ⱥ��")){
									jta.setForeground(Color.BLUE);
									for(int i=0;i<users.size();i++){
										if(users.get(i).getName().equals("Ⱥ��")){
											users.get(i).setContext(users.get(i).getContext()+strs[1]+"\n");
										}
									}
								}else 
									if(strs[0].equals("ϵͳ")){
										jta.setForeground(Color.RED);
										for(int i=0;i<users.size();i++){
											users.get(i).setContext(users.get(i).getContext()+"ϵͳ��Ϣ��"+strs[1]+"\n");
										}
									}else{
										jta.setForeground(Color.BLACK);
										String[] str_temp=strs[1].split("˵");
										System.out.println(str_temp[0]);
										for(int i=0;i<users.size();i++){
											if(users.get(i).getName().equals(str_temp[0])){
												users.get(i).setContext(users.get(i).getContext()+strs[1]+"\n");
											}
										}
									}		
							}
						}												
					}catch(Exception e){}
				} 
			}
		}).start();
		//ʵʱ��ȡѡ�����
		new Thread(new Runnable() {
			public void run() {
				while(true){
					int i ;
					choice = (String) jcb.getSelectedItem();
					for(i=0;i<users.size();i++){
						if(users.get(i).getName().equals(choice)) break;
					}
					if(i != users.size())
						jta.setText(users.get(i).getContext());
					try{
						Thread.sleep(1000);
					}catch( Exception e){}
				}
			}
		}).start();
}//���캯�����
///////////////////�����ļ�///////////////////////////			
public void send_file(String file_path) throws Exception{
	Socket new_socket = new Socket("127.0.0.1",6666);
	DataOutputStream dos = null;
	FileInputStream fis = null; 
	byte[] sendBytes = null;
	boolean bool;
	double sumL=0;
	int length=0;
	File file = new File(file_path); //Ҫ������ļ�·��  
	long file_length = file.length();
	dos = new DataOutputStream(new_socket.getOutputStream());
	fis = new FileInputStream(file);
	sendBytes = new byte[1024];
	while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0) {  
		sumL += length;    
		System.out.println("�Ѵ��䣺"+((sumL/file_length)*100)+"%");  
		dos.write(sendBytes, 0, length);  
		dos.flush();  
	} 
	if(sumL == file_length){ bool = true ;};
	if(dos!=null) 	dos.close();
	if(fis!=null) 	fis.close();
	new_socket.close();
}
////////////////////////////////�����ļ�////////////////////////////////
public void receiveFile(String filePath) throws IOException {  
	Socket s = new Socket("127.0.0.1",6666);
	byte[] inputByte = null;  
	int length = 0;  
	DataInputStream dis = null;  
 	FileOutputStream fos = null;  
 	try {  
 		dis = new DataInputStream(s.getInputStream());  
 		fos = new FileOutputStream(new File(filePath));      
 		inputByte = new byte[1024];     
 		System.out.println("�ͻ�����ʼ��������...");    
 		while ((length = dis.read(inputByte, 0, inputByte.length)) > 0) {  
 			fos.write(inputByte, 0, length);  
 			fos.flush();      
 		}  
 		System.out.println("��ɽ��գ�"+filePath);  
 		}   catch (Exception e) {  e.printStackTrace();  }  
 	if (fos != null)  
 		fos.close();  
 	if (dis != null)  
 		dis.close();  
 	s.close();
}  
//////////////////////////Users��///////////////////////
class Users{
	String context = "";
	String name = "";
	Users(String name){
		this.name = name;
	}
	public void setName(String name){
		this.name = name;
	}
	public void setContext(String context){
		this.context = context;
	}
	public String getName(){
		return this.name;
	}
	public String getContext(){
		return this.context;
	} 
}

public static void main (String[] args)  throws Exception{
	new User();
	}
}
