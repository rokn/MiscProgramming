#include <glog/logging.h>
#include <fstream>
#include <string>
#include <iostream>
#include <deque>

using namespace std;


int main(int argc, char * const argv[])
{
	google::InitGoogleLogging(argv[0]);

	ifstream ifs(argv[1]);
	string content((istreambuf_iterator<char>(ifs)),

                       (istreambuf_iterator<char>()));	

	deque<char> memory;

	int pointerPosition = 0;

	memory.push_back(0);

	for(int i = 0; i < content.length(); ++i)
	{
		switch(content[i])
		{
			case '>':
				pointerPosition++;
				//cout << "Incremented pointer" << endl;
				if(pointerPosition == memory.size())
				{
					memory.push_back(0);
				}

				break;
			case '<':
				pointerPosition--;

				//cout << "decremented pointer" << endl;
				if(pointerPosition == -1)
				{
					memory.push_front(0);
					pointerPosition++;
				}

				break;
			case '+':
				memory[pointerPosition]++;
				//cout << memory[pointerPosition] << endl;
				break;
			case '-':
				memory[pointerPosition]--;
				//cout << "decremented byte" << endl;
				break;
			case '.':
				cout << memory[pointerPosition];
				break;
			case ',':
				char ch;
				cin >> ch;
				memory[pointerPosition] = ch;
				break;
			case '[':
				if(memory[pointerPosition] == 0)
				{
					int bracketCount = 1;
					int j = i+1;
					for(; j < content.length(); ++j)
					{
						if(content[j] == '[')
						{
							bracketCount++;
						}
						else if(content[j] == ']')
						{
							bracketCount--;

							if(bracketCount < 1)
							{
								break;
							}
						}
					}

					if(bracketCount == 0)
					{
						i = j+1;
						//cout << "Jumped to: " << i << endl;
					}
					else
					{
						return EXIT_FAILURE;
					}
				}

				break;
			case ']':
				if(memory[pointerPosition] != 0)
				{
					int bracketCount = -1;
					int j = i-1;
					for(; j >= 0; --j)
					{
						if(content[j] == '[')
						{
							bracketCount++;

							if(bracketCount > -1)
							{
								break;
							}
						}
						else if(content[j] == ']')
						{
							bracketCount--;
						}
					}

					if(bracketCount == 0)
					{
						i = j;
						//cout << "Jumped to: " << i << endl;
					}
					else
					{
						return EXIT_FAILURE;
					}
				}

				break;
		}
	}


	return EXIT_SUCCESS;
}
