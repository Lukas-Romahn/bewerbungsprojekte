--------------------------------------------------------------------------------------
-- Hochschule Mannheim - Informationstechnik
-- Prof. Dr. Kurt Ackermann
-- PLB
-- Labor 5, Aufgabe 1 (Fahrstuhlsteuerung) Testbench
--
-- Anmerkung: Simulationszeit: 18 ms
--------------------------------------------------------------------------------------
LIBRARY ieee;
USE ieee.std_logic_1164.ALL;
USE ieee.numeric_std.ALL;
 
ENTITY Fahrstuhl_TB IS
GENERIC(FLOORS : natural := 5);
END Fahrstuhl_TB;
 
ARCHITECTURE behavior OF Fahrstuhl_TB IS 
 
    COMPONENT TOP
	GENERIC( 
	     g_WDT_VAL : std_logic_vector(7 downto 0):= x"20";
         g_FLOORS  : natural := 4);
    PORT(
           CLK         : in  STD_LOGIC;
           i_RST       : in  STD_LOGIC;
             -- positioning
           i_REQ_EXT   : in  STD_LOGIC_VECTOR(g_FLOORS-1 downto 0);
           i_REQ_INT   : in  STD_LOGIC_VECTOR(g_FLOORS-1 downto 0);
           i_POS       : in  STD_LOGIC_VECTOR(g_FLOORS-1 downto 0);
           o_MOTOR_DIR : out STD_LOGIC;
           o_MOTOR_EN  : out STD_LOGIC;
             -- door control
           i_DOOR_STAT : in  STD_LOGIC_VECTOR(1 downto 0);
           o_DOOR_DIR  : out STD_LOGIC;
           o_DOOR_EN   : out STD_LOGIC );
    END COMPONENT;
    
   --Inputs
   signal CLK : std_logic := '0';
   signal i_RST : std_logic := '1';
   signal i_REQ_EXT : std_logic_vector(FLOORS-1 downto 0) := (others => '0');
   signal i_REQ_INT : std_logic_vector(FLOORS-1 downto 0) := (others => '0');
   signal i_POS : std_logic_vector(FLOORS-1 downto 0) := std_logic_vector(to_unsigned(2,FLOORS));
   signal i_DOOR_STAT : std_logic_vector(1 downto 0) := "10";

 	--Outputs
   signal o_MOTOR_DIR : std_logic;
   signal o_MOTOR_EN : std_logic;
   signal o_DOOR_DIR : std_logic;
   signal o_DOOR_EN : std_logic;

   -- Clock period definitions
   constant CLK_period : time := 1 us;
   constant ZEROS : std_logic_vector(i_REQ_EXT'range):=(others => '0');
 
   -- Fahrstuhltür soll automatisch nach 1,5 ms schliessen (WDT Takt = 1MHz / 32)
   constant WDT_VAL : std_logic_vector(7 downto 0) := std_logic_vector(to_unsigned(47, 8));
   
BEGIN
 
   uut: TOP 
	GENERIC MAP (
		  g_WDT_VAL => WDT_VAL,
		  g_FLOORS  => FLOORS )
	PORT MAP (
          CLK         => CLK,
		  i_RST       => i_RST,
          i_REQ_EXT   => i_REQ_EXT,
          i_REQ_INT   => i_REQ_INT,
          i_POS       => i_POS,
          o_MOTOR_DIR => o_MOTOR_DIR,
          o_MOTOR_EN  => o_MOTOR_EN,
          i_DOOR_STAT => i_DOOR_STAT,
          o_DOOR_DIR  => o_DOOR_DIR,
          o_DOOR_EN   => o_DOOR_EN );

   CLK_process :process
   begin
		CLK <= not CLK;
		wait for CLK_period/2;
   end process;
 
	-- startup reset
	RST_GEN: process
	begin
		wait for 10 us;
		i_RST <= '0';
		wait;
	end process RST_GEN;
	
	
	-- Fahrstuhl Stim.
	ELEV: process
	begin
		-- Anfrage von OG 3
		wait for 300 us;
		i_REQ_EXT <= "10000"; 	
		wait until (i_POS="10000" and i_DOOR_STAT="01");
		i_REQ_EXT <= ZEROS;		-- Anfrage zurücksetzen
		
		-- Zieleingabe: OG 1, E
		wait for 3 ms;
		i_REQ_INT <= "00110";		
		wait until ((i_POS and i_REQ_INT)/=ZEROS and i_DOOR_STAT="01");
		i_REQ_INT <= i_REQ_INT and not(i_POS and i_REQ_INT);
		wait until ((i_POS and i_REQ_INT)/=ZEROS and i_DOOR_STAT="01");
		i_REQ_INT <= i_REQ_INT and not(i_POS and i_REQ_INT);
		
		-- Anfrage: OG 2, K
		wait for 3 ms;
		i_REQ_EXT <= "01001";		
		wait until ((i_POS and i_REQ_EXT)/=ZEROS and i_DOOR_STAT="01");
		i_REQ_EXT <= i_REQ_EXT and not(i_POS and i_REQ_EXT);
		wait until ((i_POS and i_REQ_EXT)/=ZEROS and i_DOOR_STAT="01");
		i_REQ_EXT <= i_REQ_EXT and not(i_POS and i_REQ_EXT);

		-- Anfrage aus demselben Stockwerk
		wait for 3 ms;
		i_REQ_EXT <= i_POS; 	
		wait until i_DOOR_STAT="01";
		i_REQ_EXT <= ZEROS;		-- Anfrage zurücksetzen
		wait;
	end process ELEV;
		
	MOVE: process
	begin
		-- warte auf erneute Aktivierung des Fahrstuhlmotors
		wait until o_MOTOR_EN='1';
		-- waehrend der Motor läuft --> Position veraendern
		while (o_MOTOR_EN='1') loop
			wait for 300 us;				-- Verzoegerung durch Fahrt
			if (o_MOTOR_DIR='1') then		-- Richtung: aufwaerts
				if (i_POS(i_POS'left)/='1') then
					i_POS <= i_POS(i_POS'left-1 downto 0) & '0';
				end if;
			else
				if (i_POS(i_POS'right)/='1') then		-- Richtung: runter
					i_POS <= '0' & i_POS(i_POS'left downto 1);
				end if;
			end if;
			-- nach einem Stockwerkwechsel kurz warten, falls
			-- o_MOTOR_EN zurückgesetzt wird.
			wait for 50 us;
		end loop;
	end process MOVE;
				
	DOOR: process
	begin
		wait until rising_edge(o_DOOR_EN);
		-- Tuer ist offen und soll sclhiessen
		if (o_DOOR_DIR='1' and i_DOOR_STAT="01") then 
			i_DOOR_STAT <= "00";
			wait for 100 us;
			i_DOOR_STAT <= "10";
		-- Tuer ist geschlossen und soll oeffnen
		elsif (o_DOOR_DIR='0' and i_DOOR_STAT="10") then
			i_DOOR_STAT <= "00";
			wait for 100 us;
			i_DOOR_STAT <= "01";
		end if;		
	end process DOOR;
END;
