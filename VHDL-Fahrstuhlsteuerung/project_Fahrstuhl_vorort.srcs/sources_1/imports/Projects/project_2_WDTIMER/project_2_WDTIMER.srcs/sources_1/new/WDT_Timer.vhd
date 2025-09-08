----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date: 12.11.2024 15:33:10
-- Design Name: 
-- Module Name: WDT_Timer - Behavioral
-- Project Name: 
-- Target Devices: 
-- Tool Versions: 
-- Description: 
-- 
-- Dependencies: 
-- 
-- Revision:
-- Revision 0.01 - File Created
-- Additional Comments:
-- 
----------------------------------------------------------------------------------


library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use ieee.numeric_std.ALL;

-- Uncomment the following library declaration if using
-- arithmetic functions with Signed or Unsigned values
--use IEEE.NUMERIC_STD.ALL;

-- Uncomment the following library declaration if instantiating
-- any Xilinx leaf cells in this code.
--library UNISIM;
--use UNISIM.VComponents.all;

entity WD_Timer is
generic(USE_INT_CLK: natural:=0);
  Port (
   CLK : IN std_logic ;
   Value: IN std_logic_vector(7 downto 0);
   LOAD     : IN  std_logic;
   CNT_CLK  : IN  std_logic;
   LED      : OUT std_logic;
   o_ld_dgb : OUT std_logic;
   o_RESET  : OUT std_logic 
   
   );
end WD_Timer;

architecture Behavioral of WD_Timer is

signal counter :unsigned(7 downto 0):="00000000";
signal int_CLK :std_logic ;
signal clk_choose:std_logic ;
signal ResLed:std_logic:='0';
signal clk_div: std_logic := '0';
signal counterCLK: unsigned (19 downto 0):= "00000000000000000000";
signal stored_CLK: std_logic;


begin

countCLK: process (CLK)
begin 
if rising_edge(CLK) then
    counterCLK <= counterCLK +1;
end if;
end process countCLK;

int_clk_flip: process(CLK)
begin
    if rising_edge(CLK) then
        stored_CLK <= int_CLK;
        if(LOAD='1')then
            counter<=unsigned(Value)+1;
        else  
            if(int_CLK='1' and stored_CLK = '0')then
                if(counter /= "00000000") then
                    counter<=counter-1;
                end if;
            end if;
end if;
    end if;
end process int_clk_flip;

clk_choose <= '1' when USE_INT_CLK = 1 else '0';
int_CLK <= clk_div when clk_choose = '1' else CNT_CLK;

ResLed <= '1' when counter = "00000000" else '0';

clk_div <= counterCLK(19);
o_ld_dgb<=LOAD;
LED<=ResLed;
o_RESET<=ResLed;



end Behavioral;
