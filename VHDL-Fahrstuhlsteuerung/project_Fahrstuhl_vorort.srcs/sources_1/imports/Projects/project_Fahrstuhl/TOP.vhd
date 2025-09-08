------------------------------------------------
-- Hochschule Mannheim - Informationstechnik
-- 
-- PLB
-- Labor 5, Aufgabe 1 (Fahrstuhlsteuerung)
--
------------------------------------------------
library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.NUMERIC_STD.ALL;

entity TOP is
	Generic ( 
	       g_WDT_VAL : std_logic_vector(7 downto 0):= x"20";
	       g_FLOORS  : natural := 4);
    Port ( 
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
end TOP;

architecture RTL of TOP is
    
    --Einbindung vom Watchdog Timer
    component WD_TIMER
    Port (
        CLK : IN std_logic ;
        Value: IN std_logic_vector(7 downto 0);
        LOAD     : IN  std_logic;
        CNT_CLK  : IN  std_logic;
        LED      : OUT std_logic;
        o_ld_dgb : OUT std_logic;
        o_RESET  : OUT std_logic);
    end component;
    
    --Signals
    type STATE_TYPE is (motor_on, motor_off, down, up, idle, door_open, door_close, door_opening, door_closing);
    signal STATE: STATE_TYPE := motor_off;
    signal help: STD_LOGIC_VECTOR(g_FLOORS-1 downto 0) := (others => '0');
    signal requested: STD_LOGIC_VECTOR(g_FLOORS-1 downto 0);
    
    --Signals f¸r Watchdog Timer
    signal watchDogTimer: std_logic := '0';
    signal watchDogCLKCounter : unsigned(4 downto 0) := "00000";
    signal WDT_LOAD: std_logic := '1';
    signal LED : std_logic;
    signal RESET: std_logic := '0';
    signal dummyOut: std_logic;

begin
    WDT: WD_TIMER
    port map(
        CLK => CLK,
        Value => g_WDT_VAL,
        LOAD => WDT_LOAD,
        CNT_CLK => watchDogTimer, 
        LED => LED,
        o_ld_dgb => open, --dummyOut, --open
        o_RESET => RESET 
    );
    
    --Verdrahtung f√ºr den zu hinfahrenden Stockwerk
    requested <= i_REQ_INT or i_REQ_EXT;
    
    --Automat
    process(CLK)
    begin
        if rising_edge(CLK) then
            if (i_RST ='1') then 
                STATE <= motor_off;
                o_MOTOR_EN <= '0';
            end if;
            
            --Taktteiler
            watchDogCLKCOunter <= watchDogCLKCounter + 1;
            if (watchDogCLKCounter = 16) then
                watchDogTimer <= not watchDogTimer;
                watchDogCLKCounter <= "00000";
            end if;
            
            --State ¸berg‰nge
            case STATE is
                when motor_off => if (requested = help) then STATE <= motor_off; 
                                  elsif ((requested and i_POS) = i_POS) then STATE <= door_opening;
                                  else STATE <= idle; end if;
                when down => if(requested < i_POS) then STATE <= down;
                             else STATE <= idle; end if;
                when up => if (requested > i_POS) then STATE <= up;
                           else STATE <= idle; end if;
                when idle => if (requested = help) then STATE <= motor_off;
                             elsif ((requested and i_POS)= i_POS) then STATE <= door_opening;
                             elsif (requested > i_POS) then STATE <= up;
                             elsif (requested < i_POS) then STATE <= down;  end if;
                when door_opening => if (i_DOOR_STAT = "01") then STATE <= door_open; end if;
                when door_open => if (RESET = '1')then STATE <= door_closing; end if;
                when door_closing => if (i_DOOR_STAT = "10") then STATE <= door_close; end if;
                when door_close => STATE <= idle;
                when others => STATE <= motor_off;                
            end case;
            
            --State output
            case STATE is
            when motor_off =>
                o_MOTOR_DIR <= '0';
                o_MOTOR_EN <= '0';
                o_DOOR_DIR <= '0';
                o_DOOR_EN <= '0';
                WDT_LOAD <= '1'; 
            when down =>
                o_MOTOR_DIR <= '0';
                o_MOTOR_EN <= '1';
                o_DOOR_DIR <= '0';
                o_DOOR_EN <= '0'; 
            when up =>
                o_MOTOR_DIR <= '1';
                o_MOTOR_EN <= '1';
                o_DOOR_DIR <= '0';
                o_DOOR_EN <= '0';
            when idle =>
                o_MOTOR_DIR <= '0';
                o_MOTOR_EN <= '0';
                o_DOOR_DIR <= '0';
                o_DOOR_EN <= '0';  
            when door_open =>
                o_MOTOR_DIR <= '0';
                o_MOTOR_EN <= '0';
                o_DOOR_DIR <= '0';
                o_DOOR_EN <= '0';
                WDT_LOAD <= '0';
            when door_opening =>
                o_MOTOR_DIR <= '0';
                o_MOTOR_EN <= '0';
                o_DOOR_DIR <= '0';
                o_DOOR_EN <= '1';
            when door_closing =>
                o_MOTOR_DIR <= '0';
                o_MOTOR_EN <= '0';
                o_DOOR_DIR <= '1';
                o_DOOR_EN <= '1'; 
            when door_close =>
                o_MOTOR_DIR <= '0';
                o_MOTOR_EN <= '0';
                o_DOOR_DIR <= '0';
                o_DOOR_EN <= '0';
                WDT_LOAD <= '1';
            when others =>
                o_MOTOR_DIR <= '0';
                o_MOTOR_EN <= '0';
                o_DOOR_DIR <= '0';
                o_DOOR_EN <= '0';
                WDT_LOAD <= '1'; 
            end case;
        end if;
    end process;
	
end RTL;
