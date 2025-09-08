----------------------------------------------------------------------------------
-- Hochschule Mannheim - Informationstechnik
-- PLB
-- Labor 3, Aufgabe 2 (Watchdog-Timer) -- Testbench
----------------------------------------------------------------------------------
LIBRARY ieee;
USE ieee.std_logic_1164.ALL;
 
ENTITY WDT_TB IS
END WDT_TB;
 
ARCHITECTURE behavior OF WDT_TB IS 
 
    -- component name and ports must be identical to your module
	COMPONENT WD_Timer
    Generic (USE_INT_CLK : natural := 0);
    PORT(
         CLK      : IN  std_logic;
         VALUE    : IN  std_logic_vector(7 downto 0);
         LOAD     : IN  std_logic;
         CNT_CLK  : IN  std_logic;
         LED      : OUT std_logic;
		 o_ld_dgb : OUT std_logic;
         o_RESET  : OUT std_logic );
    END COMPONENT;
    
   signal CLK, CNT_CLK : std_logic:='0';
   signal VALUE   : std_logic_vector(7 downto 0) :="00000010"; --"00111111" 3f
   signal LOAD    : std_logic:='1';
   signal o_RESET, LED, o_ld_dgb : std_logic;
   
   -- Add your signals here...


BEGIN
 
   -- instance name and ports must be identical to your module
   uut: WD_Timer
        Generic MAP (USE_INT_CLK => 1) -- modify generic value here
        PORT MAP (
          CLK      => CLK,
          VALUE    => VALUE,
          LOAD     => LOAD,
          CNT_CLK  => CNT_CLK,
          LED      => LED,
		  o_ld_dgb => o_ld_dgb,
          o_RESET  => o_RESET );

   -- Add your code here...
   
  gen_load: process
   begin
    LOAD <= '1';
    wait for 100 ns;
    LOAD <= '0';
    wait ;
   end process gen_load;
   
   generate_cntCLK:process
   begin
   
   CNT_CLK<=not CNT_CLK;
   wait for 50 ns;
   
   
   end process generate_cntCLK;
   
   --change_CLK:process
   --begin
   
   --USE_INT_CLK<=not USE_INT_CLK;
   --end process change_CLK;
   
    generate_CLK:process
   begin
   
   CLK<=not CLK;
   wait for 10 ns;
   
   
   end process generate_CLK;
   
   
END;
