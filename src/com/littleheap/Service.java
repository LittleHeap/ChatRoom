package com.littleheap;

import javax.swing.*;
import java.net.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

class Service extends JFrame{
	ArrayList<ChatThread> users = new ArrayList<ChatThread>();
	String userList = "";
	String username="";
	String choice;
	String userList_send = "";
	int temp01 = 0;
	JComboBox jcb = new JComboBox();
	JTextField jtf = new JTextField();
	JTextArea jta = new JTextArea();
	JButton jbt = new JButton();
	Service() throws Exception{
		//����������
		getContentPane().setForeground(Color.MAGENTA);
		getContentPane().setBackground(Color.WHITE);
		this.setSize(350,460);
		this.setResizable(false);
		this.setVisible(true);
		this.setTitle("������");
		setLocationRelativeTo(null); 
		getContentPane().setLayout(null);
		jtf = new JTextField();
		jtf.setFont(new Font("����", Font.PLAIN, 22));
		jtf.setBounds(0, 383, 252, 37);
		getContentPane().add(jtf);
		jtf.setColumns(10);
		jbt = new JButton("\u4E0B\u7EBF");
		jbt.setFont(new Font("��Բ", Font.PLAIN, 17));
		jbt.setForeground(Color.BLUE);
		jbt.setBackground(new Color(204, 255, 102));
		jbt.setBounds(254, 383, 90, 37);
		getContentPane().add(jbt);			
		jta = new JTextArea();
		jta.setFont(new Font("Monospaced", Font.PLAIN, 17));
		jta.setBackground(new Color(255, 255, 255));
		jta.setBounds(0, 32, 344, 345);
		jta.setBackground(new Color(51, 204, 204));
		getContentPane().add(jta);				
		jcb = new JComboBox();
		jcb.setBounds(0, 0, 344, 29);
		getContentPane().add(jcb);
		//���߰�ť����
		jbt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for(int i=0;i<users.size();i++){
					ChatThread ct = (ChatThread)users.get(i);
					if(ct.chatuser.equals(choice)){
						try {
							ct.ps.println("LOGOUT"+"#"+choice);
							users.remove(ct);
							ct.s.close();
							//�����б�
							userList_send = "";
							for(int e=0;e<users.size();e++){
								ChatThread temp = (ChatThread)users.get(e);
								if(temp.chatuser != null)
									userList_send = userList_send +temp.chatuser +"#";
							}												
							String[] strs = userList_send.split("#");
							jcb.removeAllItems();
							for(int ii=0;ii<strs.length;ii++){
								jcb.addItem(strs[ii]);
							}	
							for(int q=0;q<users.size();q++){
								ChatThread temp = (ChatThread)users.get(q);
								temp.ps.println("LOGIN#"+userList_send);
							}
						}	catch (IOException e) {}
						ct.stop();
					}
				}			
			}
		});		
		//Ⱥ����Ϣ���������
		jtf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {				
				for(int i=0;i<users.size();i++){
					ChatThread ct = (ChatThread)users.get(i);
					ct.ps.println("ϵͳ"+"#"+jtf.getText());
				}
				jta.append("��Ⱥ��ϵͳ��Ϣ��"+jtf.getText());
				jtf.setText("");
			}
		});
		//׼���߳̽����¿ͻ���
		new Thread(new Runnable() {
			public void run() {
				try{
					ServerSocket ss = new ServerSocket(9999);
					while(true){
						Socket s = ss.accept();
//															sockets.add(s);
						ChatThread ct = new ChatThread(s);
						ct.start(); 
					}
				}catch(Exception ex){}
			}
		}).start();
		//ʵʱ��ȡѡ��
		new Thread(new Runnable() {
			public void run() {
				while(true){
					choice = (String) jcb.getSelectedItem();
					try{ Thread.sleep(1000); }catch( Exception e){}
				}
			}
		}).start();
}//��	�캯������	
	
				/////////////////////////////////�����ļ�//////////////////////////////////
public void receiveFile(String filename) throws IOException { 
	System.out.println("��������ʼ��������...");   
//			    	String name = filename;
	ServerSocket new_ss= new ServerSocket(6666);
	Socket new_socket = new_ss.accept();
	byte[] inputByte = null;  
	int length = 0;  
	DataInputStream dis = null;  
	FileOutputStream fos = null;  
	String filePath = "D:/���������ݿ�/"+filename;
	try {  
		dis = new DataInputStream(new_socket.getInputStream());  
		fos = new FileOutputStream(new File(filePath));      
		inputByte = new byte[1024];     
		while ((length = dis.read(inputByte, 0, inputByte.length)) > 0) {  
			fos.write(inputByte, 0, length);  
			fos.flush();      
		}  
		System.out.println("��ɽ��գ�"+filePath);  
	}   catch (Exception e) {  e.printStackTrace();  } 
	if (fos != null)     fos.close();  
	if (dis != null)  	 dis.close();  
	new_socket.close();
	new_ss.close();
}  
///////////////////////////////�����ļ�////////////////////////////////
public void send_File(String file_name) throws Exception{
	System.out.print("������׼�������ļ�");
	ServerSocket new_ss= new ServerSocket(6666);
	Socket  new_s = new_ss.accept();
	File file = new File("D:/���������ݿ�/"+file_name); //Ҫ������ļ�·��  
	long file_length = file.length();
	DataOutputStream dos = new DataOutputStream(new_s.getOutputStream());
	FileInputStream fis = new FileInputStream(file);
	byte[] sendBytes = new byte[1024];
	int length = 0;
	int sumL = 0;
	while ((length  = fis.read(sendBytes, 0, sendBytes.length)) > 0) {  
		sumL += length;    
		System.out.println("�Ѵ��䣺"+((sumL/file_length)*100)+"%");  
		dos.write(sendBytes, 0, length);  
		dos.flush();  
	} 
	if(dos!=null) 	dos.close();
	if(fis!=null) 	fis.close();
	new_s.close();
	new_ss.close();
}			
class ChatThread extends Thread{
	Socket s;
	String chatuser;
	BufferedReader br;
	PrintStream ps;
	ChatThread(Socket s) throws Exception{
		users.add(this);
		this.s = s;
		br = new BufferedReader(new InputStreamReader(s.getInputStream()));
		ps = new PrintStream(s.getOutputStream());
	}
	public void run(){ 
		while(true){
			try{
				String massage = br.readLine();
				if(massage.startsWith("����#")){
					String[] strs_name= massage.split("#");
					for(int i=0;i<users.size();i++){
						ChatThread ct_special = (ChatThread)users.get(i);
						if(ct_special.chatuser.equals(strs_name[1])) {
							users.remove((ChatThread)ct_special);
							ct_special.s.close();
							userList_send = "";
							for(int i1=0;i1<users.size();i1++){
								ChatThread temp = (ChatThread)users.get(i1);
								if(temp.chatuser != null)
									userList_send = userList_send +temp.chatuser +"#";
							}
							String[] strs = userList_send.split("#");
							jcb.removeAllItems();
							for(int i2=0;i2<strs.length;i2++){
								jcb.addItem(strs[i2]);
							}	
							//�ͻ��˸����б�
							//��ȡ�б�ȫ������
							for(int i3=0;i3<users.size();i3++){
								ChatThread ct = (ChatThread)users.get(i3);
								ct.ps.println("LOGIN#"+userList_send);
							}													
//															break;
							ct_special.stop();
						}
					}
					//�����������б�
				}else if(massage.startsWith("LOGIN#")){
					String[] strs = massage.split("#");
					jcb.addItem(strs[1]);
					//���¶����߳�����
					this.chatuser = strs[1];					
					//��ȡ�б�ȫ������
					userList_send = "";
					for(int i=0;i<users.size();i++){
						ChatThread temp = (ChatThread)users.get(i);
						userList_send = userList_send +temp.chatuser +"#";
					}			
					//���������б����
					for(int i=0;i<users.size();i++){
						ChatThread ct = (ChatThread)users.get(i);
						ct.ps.println("LOGIN#"+userList_send);
					}
				}else if(massage.startsWith("�����ļ�#")){
					String[] strs_temp = massage.split("#");
					receiveFile(strs_temp[3]);							
					System.out.println(strs_temp[2]);
					for(int i=0;i<users.size();i++){
						ChatThread ct_temp = (ChatThread)users.get(i);
						if(ct_temp.chatuser.equals(strs_temp[2])) {
//							send_File(strs_temp[3]);
							ct_temp.ps.println("�����ļ�#"+strs_temp[3]);
							send_File(strs_temp[3]);
						}
					}
				}else{
					String[] msg = massage.split("#");
					username = msg[0];
					if(username.equals("Ⱥ��")){
						for(int i=0;i<users.size();i++){
							ChatThread ct = (ChatThread)users.get(i);
							ct.ps.println(massage);
						}
					}else{
						for(int i=0;i<users.size();i++){/////////////////////////////
							ChatThread ct_special = (ChatThread)users.get(i);
							if(ct_special.chatuser.equals(username)) {
								ct_special.ps.println(massage);
							}
						}
					}
				}//else												
			}catch(Exception ex){}
		}
	}
}
	
	
public static void main (String[] args)  throws Exception{
	new Service();
	}
}